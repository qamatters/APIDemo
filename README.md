package org.excelToCsv;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 *
 * @author javacodepoint.com
 *
 */
public class AdvancedExcelToCSVConverter {

    public static final int EXCEL_STYLE_ESCAPING = 0;
    public static final int UNIX_STYLE_ESCAPING = 1;
    private static final String DEFAULT_SEPARATOR = ",";
    private static final String CSV_FILE_EXTENSION = ".csv";
    private ArrayList<ArrayList<String>> csvData;
    private int maxRowWidth;

    /*
     * This method convert the contents of the Excel workbook into CSV format and
     * save the resulting file to the specified folder using the same name as the
     * original workbook with the .xls or .xlsx extension replaced by .csv
     */
    public void convertExcelToCSV(String strSource, String strDestination, int formattingConvention)
            throws  IOException, IllegalArgumentException {

        // Check that the source file exists.
        File sourceFile = new File(strSource);
        if (!sourceFile.exists()) {
            throw new IllegalArgumentException("The source Excel file cannot be found at " + sourceFile);
        }

        // Check that the destination folder exists to save the CSV file.
        File destination = new File(strDestination);
        if (!destination.exists()) {
            throw new IllegalArgumentException(
                    "The destination directory " + destination + " for the " + "converted CSV file(s) does not exist.");
        }
        if (!destination.isDirectory()) {
            throw new IllegalArgumentException(
                    "The destination " + destination + " for the CSV file is not a directory/folder.");
        }

        // Checking the value of formattingConvention parameter is within range.

        if (formattingConvention != AdvancedExcelToCSVConverter.EXCEL_STYLE_ESCAPING && formattingConvention != AdvancedExcelToCSVConverter.UNIX_STYLE_ESCAPING) {
            throw new IllegalArgumentException("The value passed to the "
                    + "formattingConvention parameter is out of range: " + formattingConvention + ", expecting one of "
                    + AdvancedExcelToCSVConverter.EXCEL_STYLE_ESCAPING + " or "
                    + AdvancedExcelToCSVConverter.UNIX_STYLE_ESCAPING);
        }
        FileInputStream fis = null;
        Workbook workbook = null;
        try {
            fis = new FileInputStream(sourceFile);
            System.out.println("Opening workbook [" + sourceFile.getName() + "]");
            workbook = WorkbookFactory.create(fis);

            // Convert it's contents into a CSV file
            convertToCSV(workbook);

            // Build the name of the csv folder from that of the Excel workbook.
            // Simply replace the .xls or .xlsx file extension with .csv
            String destinationFilename = sourceFile.getName();
            destinationFilename = destinationFilename.substring(0, destinationFilename.lastIndexOf('.'))
                    + CSV_FILE_EXTENSION;

            // Save the CSV file away using the newly constructed file name
            // and to the specified directory.
            saveCSVFile(new File(destination, destinationFilename), formattingConvention);

        } catch (Exception e) {
            System.out.println("Unexpected exception");
            e.printStackTrace();
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (workbook != null) {
                workbook.close();
            }
        }
    }

    /*
     * Checks to see whether the field - which consists of the formatted contents of
     * an Excel worksheet cell encapsulated within a String - contains any embedded
     * characters that must be escaped. The method is able to comply with either
     * Excel's or UNIX formatting conventions in the following manner;
     */
    private String escapeEmbeddedCharacters(String field, int formattingConvention) {

        StringBuilder buffer;

        // If the fields contents should be formatted to conform with Excel's
        // convention....
        if (AdvancedExcelToCSVConverter.EXCEL_STYLE_ESCAPING == formattingConvention) {

            // Firstly, check if there are any speech marks (") in the field;
            // each occurrence must be escaped with another set of speech marks
            // and then the entire field should be enclosed within another
            // set of speech marks. Thus, "Yes" he said would become
            // """Yes"" he said"
            if (field.contains("\"")) {
                buffer = new StringBuilder(field.replace("\"", "\\\"\\\""));
                buffer.insert(0, "\"");
                buffer.append("\"");
            } else {
                // If the field contains either embedded separator or EOL
                // characters, then escape the whole field by surrounding it
                // with speech marks.
                buffer = new StringBuilder(field);
                if ((buffer.indexOf(DEFAULT_SEPARATOR)) > -1 || (buffer.indexOf("\n")) > -1) {
                    buffer.insert(0, "\"");
                    buffer.append("\"");
                }
            }
            return buffer.toString().trim();
        }
        // The only other formatting convention this class obeys is the UNIX one
        // where any occurrence of the field separator or EOL character will
        // be escaped by preceding it with a backslash.
        else {
            if (field.contains(DEFAULT_SEPARATOR)) {
                field = field.replaceAll(DEFAULT_SEPARATOR, ("\\\\" + DEFAULT_SEPARATOR));
            }
            if (field.contains("\n")) {
                field = field.replace("\n", "\\\\\n");
            }
            return field;
        }
    }

    /*
     * Called to convert the contents of the currently opened workbook into a CSV
     * file.
     */
    private void convertToCSV(Workbook workbook) {

        // Create the FormulaEvaluator and DataFormatter instances
        // that will be needed to, respectively,
        // force evaluation of formulae found in cells and create a
        // formatted String encapsulating the cells contents.
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        DataFormatter formatter = new DataFormatter(true);

        // Initialize csvData with empty
        this.csvData = new ArrayList<>();

        System.out.println("Converting files contents to CSV format.");

        // Discover how many sheets there are in the workbook and then iterate through
        // them.
        int numSheets = workbook.getNumberOfSheets();
        System.out.println("No of sheets :" + numSheets);

        for (int i = 0; i < numSheets; i++) {

            // Get a reference to a sheet and check to see if it contains any rows.
            Sheet sheet = workbook.getSheetAt(i);
            if (sheet.getPhysicalNumberOfRows() > 0) {
                // Note down the index number of the bottom-most row and
                // then iterate through all of the rows on the sheet starting
                // from the very first row - number 1 - even if it is missing.
                // Recover a reference to the row and then call another method
                // which will strip the data from the cells and build lines
                // for inclusion in the restyling CSV file.
                int lastRowNum = sheet.getLastRowNum();
                System.out.println("last row num is:" + lastRowNum);
                for (int j = 0; j <= lastRowNum; j++) {
                    Row row = sheet.getRow(j);
                    ArrayList<String> csvLine = new ArrayList<>();
                    System.out.println(" row is :" + j);
                    // Get the index for the right most cell on the row and then
                    // step along the row from left to right recovering the contents
                    // of each cell, converting that into a formatted String and
                    // then storing the String into the csvLine ArrayList.
                    System.out.println("row value is :" + row);
                    int lastCellNum = row.getLastCellNum();
                    for (int k = 0; k <= lastCellNum; k++) {
                        Cell cell = row.getCell(k);
                        if (cell == null) {
                            csvLine.add("");
                        } else {
                            if (cell.getCellType() != CellType.FORMULA) {
                                csvLine.add(formatter.formatCellValue(cell));
                            } else {
                                csvLine.add(formatter.formatCellValue(cell, evaluator));
                            }
                        }
                    }
                    // Make a note of the index number of the right most cell. This value
                    // will later be used to ensure that the matrix of data in the CSV file
                    // is square.
                    if (lastCellNum > this.maxRowWidth) {
                        this.maxRowWidth = lastCellNum;
                    }

                    this.csvData.add(csvLine);
                }
            }
        }
    }

    /*
     * Called to actually save the data recovered from the Excel workbook as a CSV
     * file.
     */
    private void saveCSVFile(File file, int formattingConvention) throws IOException {
        ArrayList<String> line;
        StringBuilder buffer;
        String csvLineElement;

        // Open a writer onto the CSV file.
        try (BufferedWriter bw = Files.newBufferedWriter(file.toPath(), StandardCharsets.ISO_8859_1)) {

            System.out.println("Saving the CSV file [" + file.getName() + "]");

            // Step through the elements of the ArrayList that was used to hold
            // all of the data recovered from the Excel workbooks' sheets, rows
            // and cells.
            for (int i = 0; i < this.csvData.size(); i++) {
                buffer = new StringBuilder();

                // Get an element from the ArrayList that contains the data for
                // the workbook.
                line = this.csvData.get(i);
                for (int j = 0; j < this.maxRowWidth; j++) {
                    if (line.size() > j) {
                        csvLineElement = line.get(j);
                        if (csvLineElement != null) {
                            buffer.append(escapeEmbeddedCharacters(csvLineElement, formattingConvention));
                        }
                    }
                    if (j < (this.maxRowWidth - 1)) {
                        buffer.append(DEFAULT_SEPARATOR);
                    }
                }

                // Once the line is built, write it away to the CSV file.
                bw.write(buffer.toString().trim());

                // Condition the inclusion of new line characters so as to
                // avoid an additional, superfluous, new line at the end of
                // the file.
                if (i < (this.csvData.size() - 1)) {
                    bw.newLine();
                }
            }
        }
    }

    /*
     * Testing the Excel to CSV converter program using the main method
     */
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        boolean converted = true;
        try {
            AdvancedExcelToCSVConverter converter = new AdvancedExcelToCSVConverter();
            String strSource = "src/main/resources/Excel/sampleData.xlsx";
            String strDestination = "src/main/resources/CSV/";
            converter.convertExcelToCSV(strSource, strDestination,  EXCEL_STYLE_ESCAPING);
        } catch (Exception e) {
            System.out.println("Unexpected exception");
            e.printStackTrace();
            converted = false;
        }

        if (converted) {
            System.out.println("Conversion took " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds");
        }
    }

}
