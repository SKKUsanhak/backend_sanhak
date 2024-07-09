package blueprint_table_ocr.webserver.azure;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClient;
import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClientBuilder;
import com.azure.ai.formrecognizer.documentanalysis.models.AnalyzeResult;
import com.azure.ai.formrecognizer.documentanalysis.models.DocumentTable;
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
    
    public List<DocumentTable> analyzeTable(MultipartFile file) throws IOException { // 파일을 받아서 OCR 수행하는 메인 메소드
        setLayoutDocumentData(FileToBinaryData(file));
        syncPoller = documentAnalysisClient.beginAnalyzeDocument("prebuilt-layout", layoutDocumentData);
        AnalyzeResult analyzeLayoutResult = syncPoller.getFinalResult();
        TableToCSV(analyzeLayoutResult.getTables());
        return analyzeLayoutResult.getTables();
    }
    
    public BinaryData FileToBinaryData(MultipartFile file) throws IOException { // pdf 파일을 OCR을 위해 BinaryData 형태로 바꿔주는 메소드
        byte[] fileBytes = file.getBytes();
        return BinaryData.fromBytes(fileBytes);
    }
    
    public void TableToCSV(List<DocumentTable> documentTables) throws IOException { // SyncPoller가 인식한 여러가지 데이터 중 table 형태를 CSV 파일로 전환
        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:/Users/임형준/Desktop/산학/test.csv")
        		,StandardCharsets.UTF_8));
             CSVPrinter csvPrinter = new CSVPrinter(bufferedWriter, CSVFormat.DEFAULT)) {

            for (DocumentTable table : documentTables) {
                processTable(table, csvPrinter);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    // 원래 있던 기능이었는데 삭제한 것 
    // 0,0을 자동으로 제목으로 인식하는 기능
    // 1열~2열을 자동으로 행 데이터로 인식하여 병합 (-> 3열짜리 행 데이터가 있는 테이블이 있어서 잠시 제거함, 사용자 검수 기능으로 바꿀 예정)
    // 빗금 그어진 열 결측치 퍼센트 기준으로 자동 삭제 기능 -> 사용자 검수 기능으로 통합 예정 (열 자동 병합을 삭제하면서 이것도 잠시 삭제)
    private static void processTable(DocumentTable table, CSVPrinter csvPrinter) throws IOException { // DocumentTable 클래스 내부 데이터를 CSV 파일로 전환
        List<List<String>> tableData = new ArrayList<>();

        table.getCells().forEach(cell -> {
            int rowIndex = cell.getRowIndex();
            int colIndex = cell.getColumnIndex();
            String content = cell.getContent().replace(":selected:", "").replace(":unselected:", "").replace("\n", " "); // 필요없는 문자 제거

            while (tableData.size() <= rowIndex) {
                List<String> newRow = new ArrayList<>(Collections.nCopies(table.getColumnCount(), "")); // 빈 칸은 ""로 채움
                tableData.add(newRow);
            }
            tableData.get(rowIndex).set(colIndex, content);
        });
        
        List<Integer> removeIndex = new ArrayList<>();
        for (int i = 1; i < tableData.size(); i++) {
            List<String> rowData = tableData.get(i);
            int cnt = 0;
            int cnt_col = 0;
            for (int j = 0; j < rowData.size(); j++) {
                if (!rowData.get(j).isEmpty()) { // 비어 있지 않은지 확인할 때는 isEmpty() 메서드를 사용하는 것이 좋습니다.
                    cnt++;
                    cnt_col = j;
                }
            }
            if (cnt == 1) { // 1개 제외 모든 값이 "" 이면 위 열과 자동 병합
                tableData.get(i - 1).set(cnt_col, tableData.get(i - 1).get(cnt_col) + rowData.get(cnt_col));
                removeIndex.add(i);
            }
        }
        Collections.sort(removeIndex, Collections.reverseOrder());
        for (int index : removeIndex) {
            tableData.remove(index);
        }
        
        for (List<String> rowData : tableData) {
            csvPrinter.printRecord(rowData);
        }
        csvPrinter.println();  // 테이블 사이에 공백 행 추가
    }
}
