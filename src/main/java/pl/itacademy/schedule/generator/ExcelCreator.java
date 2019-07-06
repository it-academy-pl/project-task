package pl.itacademy.schedule.generator;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pl.itacademy.schedule.util.PropertiesReader;

import java.time.format.DateTimeFormatter;

public class ExcelCreator {

	private Workbook workbook;
	private CellStyle cellStyleRight;
	private CellStyle cellStyleLeft;
	private CellStyle cellStyleLeftBold;
	private CellStyle cellStyleRightBold;

	public Workbook createWorkbook(Schedule schedule) {
		workbook = new XSSFWorkbook();
		createCellStyles();
		Sheet sheet = workbook.createSheet("Schedule");

		PropertiesReader propertiesReader = PropertiesReader.getInstance();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(propertiesReader.readProperty("dateFormat"));
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(propertiesReader.readProperty("timeFormat"));

		int rowNum = 0;
		for (Lesson day : schedule.getLessons()) {

			setCellValue(sheet, rowNum, 0, day.getDate().format(dateFormatter), cellStyleRight);
			setCellValue(sheet, rowNum, 3, day.getBeginTime().format(timeFormatter), cellStyleRight);
			setCellValue(sheet, rowNum, 4, day.getEndTime().format(timeFormatter), cellStyleRight);

			rowNum++;
		}

		setCellValue(sheet, 0, 7, "hours done", cellStyleRight);
		setCellFormula(sheet, 0, 8, "SUM(F1:F57)", cellStyleLeft);

		setCellValue(sheet, 1, 7, "hours planned", cellStyleRight);
		setCellValue(sheet, 1, 8, schedule.getNumberOfHours(), cellStyleLeft);

		setCellValue(sheet, 3, 7, "lessons done", cellStyleRight);
		setCellFormula(sheet, 3, 8, "COUNTIF(B1:B57,\"done\")", cellStyleLeft);

		setCellValue(sheet, 4, 7, "lessons planned", cellStyleRight);
		setCellValue(sheet, 4, 8, schedule.getLessons().size(), cellStyleLeft);

		setCellValue(sheet,14,7,"STATUS:",cellStyleRightBold);
		setCellFormula(sheet,14,8,"IF(I1=I2,\"COMPLETED\",\"IN PROGRESS\")",cellStyleLeftBold);
		
		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4);
		sheet.autoSizeColumn(7);
		sheet.autoSizeColumn(8);
		
		return workbook;
	}

	private void setCellValue(Sheet sheet, int row, int column, String value, CellStyle style) {
		Cell cell = getOrCreateRowAndCell(sheet, row, column);
		cell.setCellValue(value);
		cell.setCellStyle(style);
	}

	private void setCellValue(Sheet sheet, int row, int column, int value, CellStyle style) {
		Cell cell = getOrCreateRowAndCell(sheet, row, column);
		cell.setCellValue(value);
		cell.setCellStyle(style);
	}

	private void setCellFormula(Sheet sheet, int row, int column, String formula, CellStyle style) {
		Cell cell = getOrCreateRowAndCell(sheet, row, column);
		cell.setCellFormula(formula);
		cell.setCellStyle(style);
	}

	private Cell getOrCreateRowAndCell(Sheet sheet, int rowNumber, int columnNumber) {
		Row row = getOrCreateRow(sheet, rowNumber);
		Cell cell = getOrCreateCell(row, columnNumber);
		return cell;
	}

	private Row getOrCreateRow(Sheet sheet, int rowNumber) {
		Row row = sheet.getRow(rowNumber);
		if (row == null)
			row = sheet.createRow(rowNumber);
		return row;
	}

	private Cell getOrCreateCell(Row row, int columnNumber) {
		Cell cell = row.getCell(columnNumber);
		if (cell == null)
			cell = row.createCell(columnNumber);
		return cell;
	}

	private void createCellStyles() {
		cellStyleRight = workbook.createCellStyle();
		cellStyleRight.setAlignment(HorizontalAlignment.RIGHT);
		
		cellStyleLeft = workbook.createCellStyle();
		cellStyleLeft.setAlignment(HorizontalAlignment.LEFT);
		
		cellStyleLeftBold = workbook.createCellStyle();
		cellStyleLeftBold.cloneStyleFrom(cellStyleLeft);
		Font font = workbook.createFont();
		font.setFontName("Calibri");
		font.setFontHeightInPoints((short)11);
		font.setBold(true);
		cellStyleLeftBold.setFont(font);
		
		cellStyleRightBold = workbook.createCellStyle();
		cellStyleRightBold.cloneStyleFrom(cellStyleRight);
		cellStyleRightBold.setFont(font);
	
	}
}
