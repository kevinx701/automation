/**
 * 
 */
package com.test.qa.automtion.datafactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

class Excel {
  
    private String filePath;
    private Sheet workSheet;
    private Workbook workbook;
    private String extention;
    private InputStream inputStream;
    
    public Excel(String filePath) {
    	this.filePath = filePath;
    }

    public String getFile() {
        return filePath;
    }

    public void setWorkBook() {
        String name = new File(filePath).getName();
        extention = name.substring(name.lastIndexOf(".") + 1);
        
        try {
			inputStream = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        if (extention.equals("xls")) {
            try {
				setWorkBook2003();
			} catch (IOException e) {
				e.printStackTrace();
			}
        } else if (extention.equals("xlsx")) {
            try {
				setWorkBook2007();
			} catch (InvalidFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        } else {
            throw new IllegalArgumentException("POI cannot resovle your excel file version");
        }

    }

    public void setExtention(String ext) {
        extention = ext;
    }

    public void setWorkBook2003() throws IOException {
        inputStream = new FileInputStream(filePath);
        workbook = new HSSFWorkbook(inputStream);
    }

    public void setWorkBook2007() throws IOException, InvalidFormatException {
        inputStream = new FileInputStream(filePath);
        workbook = new XSSFWorkbook(OPCPackage.open(inputStream));
    }

    public Workbook getWookBook() {
        return workbook;
    }

    public void setSheet(String sheet) {
        workSheet = workbook.getSheet(sheet);
    }

    public void setSheet(HSSFSheet sheet) {
        workSheet = sheet;
    }

    public void setSheet(XSSFSheet sheet) {
        workSheet = sheet;
    }

    public Sheet getSheet() {
        return workSheet;
    }

    // Line 1 default as keys(IDs) set, data start from Line 2
    public Row getRow(int row) {
        return workSheet.getRow(row);
    }

    // Line 1 default as keys(IDs) set, data start from Line 2
    public Row getRow(String id, String value) {
        Row row = workSheet.getRow(0);
        int position = getRowPosition(row, id);
        for (int i = 1; i < workSheet.getLastRowNum(); i++) {
            row = workSheet.getRow(i);
            if (row.getCell(position).getStringCellValue() == value) {
                return row;
            }
        }
        return null;
    }

    // Line 1 default as keys(IDs) set, data start from Line 2
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public HashMap getRowVals(int row) {
        ArrayList<String> keys = getRowdata(getRow(0));
        ArrayList<String> values = getRowdata(getRow(row));
        HashMap result = new HashMap();
        int i = 0;
        for (String value : values) {
            result.put(keys.get(i), value);
            i++;
        }
        return result;
    }

    // Line 1 default as keys(IDs) set, data start from Line 2
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public HashMap getRowVals(String id, String value) {
        ArrayList<String> keys = getRowdata(getRow(0));
        ArrayList<String> values = getRowdata(getRow(id, value));
        HashMap result = new HashMap();
        int i = 0;
        for (String key : keys) {
            result.put(key, values.get(i));
            i++;
        }
        return result;
    }

    public Cell getCell(Row row, int colNum) {
        return row.getCell(colNum);
    }

    public Cell getCell(int rowNum, int colNum) {
        return getRow(rowNum).getCell(colNum);
    }

    public Cell getCell(String id, String key, int colnum) {
        return getRow(id, key).getCell(colnum);
    }

    public Cell getCell(String id, String key, String colName) {
        return getRow(id, key).getCell(getRowPosition(getRow(0), colName));
    }

    public String getCellVal(Row row, int num) {
        return getCellValue(row.getCell(num));
    }

    public String getCellVal(int rowNum, int colNum) {
        return getCellValue(getRow(rowNum).getCell(colNum));
    }

    public String getCellVal(String id, String key, int colnum) {
        return getCellValue(getRow(id, key).getCell(colnum));
    }

    public String getCellVal(String id, String key, String colName) {
        return getCellValue(getRow(id, key).getCell(getRowPosition(getRow(0), colName)));
    }

    public void closeInput() throws IOException {
        inputStream.close();
    }

    public void writeBook() throws IOException {
        FileOutputStream fileOut = new FileOutputStream(filePath);
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }

    private int getRowPosition(Row row, String id) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            if (row.getCell(i).getStringCellValue() == id) {
                return i;
            }
        }
        return 0;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private ArrayList getRowdata(Row row) {
        ArrayList<String> values = new ArrayList();
        for (int i = 0; i < row.getLastCellNum(); i++) {
            if (row.getCell(i) != null) {
                values.add(getCellValue(row.getCell(i)));
            } else {
                values.add(null);
            }
        }
        return values;
    }

    public String getCellValue(Cell cell) {

        switch (cell.getCellType()) {
        case Cell.CELL_TYPE_STRING:
            return cell.getRichStringCellValue().getString();
        case Cell.CELL_TYPE_NUMERIC:
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue().toString();
            } else {
                return Double.toString(cell.getNumericCellValue());
            }
        case Cell.CELL_TYPE_BOOLEAN:
            return Boolean.toString(cell.getBooleanCellValue());
        case Cell.CELL_TYPE_FORMULA:
            return cell.getCellFormula();
        default:
            return null;
        }
    }

    public static void main(String[] args) throws IOException, InvalidFormatException {
        /*
         * Sample
         */
        Excel Sample = new Excel("src\\test\\resources\\bulk.xls");
        /// api/src/test/resources/asset_json_structure.xlsx
        Sample.setWorkBook();
        Sample.setSheet("success_cases");
        System.out.println(Sample.getRowVals(2));
        System.out.println( Sample.getCellVal(Sample.getRow(2),3));

        // println "**********************2007****************************"
        Excel e07 = new Excel("src\\test\\resources\\bulk.xlsx");
        /// api/src/test/resources/asset_json_structure.xlsx
        e07.setWorkBook();
        e07.setSheet("success_cases");
        // println e07.getRowVals("metadataLevel", "full").sort();
        // println e07.getCell("metadataLevel", "full", 3)
        // println e07.getCell("metadataLevel", "full", "Test Comment").getClass()
    }

}
