package blueprint_table_ocr.webserver.azure.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClient;
import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClientBuilder;
import com.azure.ai.formrecognizer.documentanalysis.models.AnalyzeResult;
import com.azure.ai.formrecognizer.documentanalysis.models.DocumentTable;
import com.azure.ai.formrecognizer.documentanalysis.models.DocumentTableCell;
import com.azure.ai.formrecognizer.documentanalysis.models.OperationResult;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.BinaryData;
import com.azure.core.util.polling.SyncPoller;

@Service
public class AzureService {
    
    private DocumentAnalysisClient documentAnalysisClient; // Azure API client
    private SyncPoller<OperationResult, AnalyzeResult> syncPoller; // OCR 분석기능
    private BinaryData layoutDocumentData; // syncPoller 호출을 위해 필요 pdf 파일을 binaryData로 변환한 것 
    
    public AzureService() { // 시작 시 API 엔드포인트 / 키 고정
        super();
        this.documentAnalysisClient = new DocumentAnalysisClientBuilder()
                .credential(new AzureKeyCredential("3a72f263d819435fb93dba4a6293780f"))
                .endpoint("https://eastus.api.cognitive.microsoft.com/")
                .buildClient();
    }

    public BinaryData getLayoutDocumentData() {
        return layoutDocumentData;
    }

    public void setLayoutDocumentData(BinaryData layoutDocumentData) {
        this.layoutDocumentData = layoutDocumentData;
    }
    
    public XSSFWorkbook analyzeTable(MultipartFile file) throws IOException { // 파일을 받아서 OCR 수행하는 메인 메소드
        setLayoutDocumentData(FileToBinaryData(file));
        syncPoller = documentAnalysisClient.beginAnalyzeDocument("prebuilt-layout", layoutDocumentData);
        AnalyzeResult analyzeLayoutResult = syncPoller.getFinalResult();
        List<DocumentTable> documentTables = analyzeLayoutResult.getTables();
        XSSFWorkbook workbook = TableToXLSX(documentTables);
        return workbook;
    }
    
    public BinaryData FileToBinaryData(MultipartFile file) throws IOException { // pdf 파일을 OCR을 위해 BinaryData 형태로 바꿔주는 메소드
        byte[] fileBytes = file.getBytes();
        return BinaryData.fromBytes(fileBytes);
    }
    
    public XSSFWorkbook TableToXLSX(List<DocumentTable> documentTables) throws IOException { // SyncPoller가 인식한 여러가지 데이터 중 table 형태를 XLSX 파일로 전환
    	
    	XSSFWorkbook workbook = new XSSFWorkbook();
    	for(int i = 0; i < documentTables.size(); i++) {
    		
    		DocumentTable table = documentTables.get(i);
    		XSSFSheet sheet = workbook.createSheet("Table_" + i); // 테이블마다 신규 시트로 분리
    		
    		for(int j = 0; j < table.getRowCount(); j++) { // 열 개수를 받아 열 추가
    			Row row = sheet.createRow(j);
    			for(int k = 0; k < table.getColumnCount(); k++) { 
    				row.createCell(k);
    			}
    		}
    		
    		List<DocumentTableCell> cells = table.getCells(); // 셀 리스트 
    		for(DocumentTableCell cell : cells) {
    			int row = cell.getRowIndex();
    			int col = cell.getColumnIndex();
    			String data = cell.getContent();
    			data = data.replace(":selected:", "").replace(":unselected:", "").replace("\n", " ").replace("  ", " "); // 필요없는 문자 제거
    			// 필요한 셀 정보 리턴
    			Cell xlsxCell = sheet.getRow(row).createCell(col); // 셀 생성
    			xlsxCell.setCellValue(data);
    		}
    		sheet = processSheet(sheet);
    	}
         return workbook;
    }
    
    public XSSFSheet processSheet(XSSFSheet sheet) throws IOException {
    	// 기호 부분 인식을 잘못해서 셀이 2개로 나뉘는 경우를 막기 위한 간단한 후처리
        int rowCnt = sheet.getPhysicalNumberOfRows();
        List<Integer> emptyRow = new ArrayList<>();

        for (int i = 1; i < rowCnt; i++) { // 제목 행은 항상 셀이 하나이므로 오류 방지를 위해 연산 X
            Row xlsxRow = sheet.getRow(i);
            if (xlsxRow == null) continue; // Row가 null일 경우 건너뛰기

            int notNaCnt = 0; // 비지 않은 셀 카운트
            int cellIdx = -1;

            for (int j = 0; j < xlsxRow.getPhysicalNumberOfCells(); j++) {
                Cell cell = xlsxRow.getCell(j);
                if (cell != null && cell.getCellType() != CellType.BLANK && !cell.getStringCellValue().isEmpty()) {
                    notNaCnt++;
                    cellIdx = j;
                }
            }

            if (notNaCnt == 1 && cellIdx >= 0) { // 한 셀만 데이터가 있다면 해당 셀 위 행의 셀과 합치고 해당 행 삭제
            	// 잘린 셀이 숫자일때만 작동하도록 변경
                Row previousRow = sheet.getRow(i - 1);
                if (previousRow == null) continue; // 이전 행이 null일 경우 건너뛰기

                Cell targetCell = previousRow.getCell(cellIdx); // 위 행의 해당 열의 셀
                if (targetCell == null) {
                    targetCell = previousRow.createCell(cellIdx);
                }
                
                Cell currentCell = xlsxRow.getCell(cellIdx); // 현재 셀
                if (currentCell != null && currentCell.getStringCellValue().charAt(0) >= '0' && currentCell.getStringCellValue().charAt(0) <= '9') {
                    targetCell.setCellValue((targetCell.getStringCellValue() + ' ' + currentCell.getStringCellValue()).replace("  ", " ")); // 셀 합칠 때 빈칸을 두고 합치되 빈칸이 2칸이 되면 한칸으로 수정
                    emptyRow.add(i);
                }
            }
        }

        for (int i = emptyRow.size() - 1; i >= 0; i--) { // 뒤에서부터 삭제
            int rowIndex = emptyRow.get(i);
            sheet.removeRow(sheet.getRow(rowIndex));
            if (rowIndex < rowCnt - 1) {
                sheet.shiftRows(rowIndex + 1, rowCnt, -1);
            }
        }
        return sheet;
    }
    
    public boolean uploadToDB(MultipartFile file) {
    	// 파일 저장 경로 설정
        String uploadDir = "C:/Users/임형준/Desktop/산학/webserver/webserver";
        Path uploadPath = Paths.get(uploadDir);

        // 디렉토리가 존재하지 않으면 생성
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        // 파일 경로 설정
        Path filePath = uploadPath.resolve(file.getOriginalFilename());

        try {
            // 파일을 로컬에 저장 (기존 파일이 있을 경우 덮어쓰기)
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        // 파일을 저장하는 이유는 결과를 확인하기 위해서 
        
        
    }
    
}

