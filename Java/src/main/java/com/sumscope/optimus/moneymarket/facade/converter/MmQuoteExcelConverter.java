package com.sumscope.optimus.moneymarket.facade.converter;

import com.sumscope.optimus.commons.exceptions.BusinessRuntimeException;
import com.sumscope.optimus.commons.exceptions.GeneralValidationErrorType;
import com.sumscope.optimus.commons.exceptions.ValidationExceptionDetails;
import com.sumscope.optimus.moneymarket.commons.enums.Direction;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteTimePeriod;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteType;
import com.sumscope.optimus.moneymarket.commons.exception.MMExceptionCode;
import com.sumscope.optimus.moneymarket.model.dbmodel.ExcelFile;
import com.sumscope.optimus.moneymarket.model.dbmodel.User;
import com.sumscope.optimus.moneymarket.model.dto.InstitutionDto;
import com.sumscope.optimus.moneymarket.model.dto.MmQuoteDetailsDto;
import com.sumscope.optimus.moneymarket.model.dto.MmQuoteDto;
import com.sumscope.optimus.moneymarket.service.MmAllianceInstitutionService;
import com.sumscope.optimus.moneymarket.service.UserBaseService;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Created by fan.bai on 2016/7/29.
 * Excel文件至MmQuoteDto的转换器
 */
@Component
public class MmQuoteExcelConverter {
    @Autowired
    private InstitutionConverter institutionConverter;

    @Autowired
    private MmAllianceInstitutionService mmAllianceInstitutionService;

    @Autowired
    private UserBaseService userBaseService;


    private static final String IN = "收";
    private static final String OUT = "出";
    private static final String OLDEXCLE = "xls";
    private static final String NEWEXCEL = "xlsx";
    private static final int FOUR = 4;
    private static final String D = "D";
    private static final String DD = "DD";
    private static final String MM = "MM";
    private static final String M = "M";
    private static final String YY = "YY";
    private static final String Y = "Y";

    /**
     * 根据excel文件内容解析生成前台报价单列表
     *
     * @param excelFileContent excel文件内容，从web端传入
     * @param invalids         一个调用方生成的list，用以记录可能的解析错误
     * @return 解析出的MmQuoteDto数据
     */
    public List<MmQuoteDto> parserQuoteInExcelFile(ExcelFile excelFileContent, List<ValidationExceptionDetails> invalids, String userId) {
        int start = excelFileContent.getData().indexOf("base64,") + "base64,".length();
        String fileContent = excelFileContent.getData().substring(start);
        byte[] excelFileContents = Base64.getDecoder().decode(fileContent);
        List<MmQuoteDto> mmQuotes = new ArrayList<MmQuoteDto>();
        //打开excel工作簿
        String fileName = excelFileContent.getFileName().substring(excelFileContent.getFileName().lastIndexOf(".") + 1); //获取文件后缀名
        InputStream in = new ByteArrayInputStream(excelFileContents);
        Sheet sheet;
        if (fileName.equals(OLDEXCLE)) {
            HSSFWorkbook workbook = (HSSFWorkbook) createWorkbook(in);
            sheet = workbook.getSheetAt(0); //拿到第一个sheet页
            getRowsAndCell(invalids, sheet, mmQuotes, userId);
        } else if (fileName.equals(NEWEXCEL)) {
            XSSFWorkbook workbook = (XSSFWorkbook) createWorkbook(in);
            sheet = workbook.getSheetAt(0); //拿到第一个sheet页
            getRowsAndCell(invalids, sheet, mmQuotes, userId);
        } else {
            throw new BusinessRuntimeException(MMExceptionCode.EXCEL_ERROR, "您上传的文件格式有误!!!");
        }
        //将相同的报价的sequence字段递增.
        for (int i = 0; i < mmQuotes.size(); i++) {
            int temp = 0;
            for (int j = i + 1; j < mmQuotes.size(); j++) {
                if (mmQuotes.get(i).getInstitutionName().equals(mmQuotes.get(j).getInstitutionName()) && mmQuotes.get(i).getSequence() == mmQuotes.get(j).getSequence()
                        ) {
                    mmQuotes.get(j).setSequence(++temp);
                }
            }
        }
        return mmQuotes;
    }

    private void getRowsAndCell(List<ValidationExceptionDetails> invalids, Sheet sheet, List<MmQuoteDto> mmQuotes, String userId) {
        int index = 0; //记录集合中异常的数量
        int lastIndex = 0; //记录列名为备注的最后一列的位置.
        for (int j = 0; j <= sheet.getRow(2).getLastCellNum(); j++) {
            sheet.getRow(2).getCell(j).setCellType(HSSFCell.CELL_TYPE_STRING);
            if (sheet.getRow(2).getCell(j).getStringCellValue().equals("备注")) {
                lastIndex += j;
                break;
            }
        }
        for (Row row : sheet) {
            if (row.getRowNum() < 3) {
                continue;
            } //表格内容从第四行开始.
            if (row.getCell(0).getCellType() == 3 || (row.getCell(0).getCellType() != 3 && row.getCell(1).getCellType() == 3 && row.getCell(2).getCellType() == 3)) {
                continue;
            } //如果这一行的 机构没有,进出方向没有,直接略过,不保存.
            List<MmQuoteDetailsDto> results = new ArrayList<MmQuoteDetailsDto>();
            MmQuoteDto mmQuoteDto = new MmQuoteDto();
            row.getCell(1).setCellType(HSSFCell.CELL_TYPE_STRING);
            if (row.getCell(1) != null && !row.getCell(1).getStringCellValue().equals("")) {
                //获取该用户的联盟机构
                User user = userBaseService.retreiveAllUsersGroupByUserID().get(userId);
                if (user != null) {
                    List<InstitutionDto> avaliableInstitutions = institutionConverter.convertInstitutionDtos(mmAllianceInstitutionService.getAllianceInstitutionsByGivenInstitutionId(user.getCompanyId()),userId);
                    for (InstitutionDto institutionDto : avaliableInstitutions) {
                        if (row.getCell(1).getStringCellValue().trim().equals(institutionDto.getDisplayName())) {
                            mmQuoteDto.setInstitutionId(institutionDto.getInstitutionId());
                            mmQuoteDto.setInstitutionName(institutionDto.getDisplayName());
                            break;
                        }
                    }
                }
                if (mmQuoteDto.getInstitutionId() == null) {
                    invalids.add(new ValidationExceptionDetails(GeneralValidationErrorType.DATA_INVALID, String.valueOf(row.getRowNum() + 1), "该机构不存在于您的联盟机构列表中,请重新导入"));
                }

            } else {
                invalids.add(new ValidationExceptionDetails(GeneralValidationErrorType.DATA_MISSING, "{"+String.valueOf(row.getRowNum() + 1)+","+2+"}", "数据错误"));
            }
            row.getCell(2).setCellType(HSSFCell.CELL_TYPE_STRING);
            if (row.getCell(2).getStringCellValue().trim().equals(IN)) {
                mmQuoteDto.setDirection(Direction.IN); //设置进出方向
            } else if (row.getCell(2).getStringCellValue().trim().equals(OUT)) {
                mmQuoteDto.setDirection(Direction.OUT);
            } else {
                invalids.add(new ValidationExceptionDetails(GeneralValidationErrorType.DATA_MISSING, "{"+String.valueOf(row.getRowNum() + 1)+","+3+"}", "数据错误"));

            }
            //使用列名为备注的该列作为备注.
            mmQuoteDto.setMemo(row.getCell(lastIndex).getStringCellValue());
            //获取报价天数 和利率部分.
            QuoteType quoteType = null;
            if (sheet.getRow(1).getCell(3).getStringCellValue().equals("类型")) {
                if (row.getCell(3).getStringCellValue().equals("非保R2")) {
                    quoteType = QuoteType.UR2;
                }
                if (row.getCell(3).getStringCellValue().equals("非保R3")) {
                    quoteType = QuoteType.UR3;
                }
                if (row.getCell(3).getStringCellValue().equals("保本")) {
                    quoteType = QuoteType.GTF;
                }
                if (row.getCell(3).getStringCellValue().equals("同存")) {
                    quoteType = QuoteType.IBD;
                }
                mmQuoteDto.setQuoteType(quoteType);
                if (mmQuoteDto.getQuoteType() == null) {
                    invalids.add(new ValidationExceptionDetails(GeneralValidationErrorType.DATA_MISSING, "{"+String.valueOf(row.getRowNum() + 1)+","+4+"}", "数据错误"));
                }
            }
            getMmQuoteDetails(invalids, sheet, row, results, lastIndex);
            mmQuoteDto.setQuoteDetailsDtos(results);
            if (invalids.size() <= index) {
                mmQuotes.add(mmQuoteDto);
            }
            index = invalids.size();
        }
    }

    private void getMmQuoteDetails(List<ValidationExceptionDetails> invalids, Sheet sheet, Row row, List<MmQuoteDetailsDto> results, int lastIndex) {
        BigDecimal price;
        for (int i = 4; i < lastIndex; i++) {
            MmQuoteDetailsDto mmQuoteDetailsDto = new MmQuoteDetailsDto();
            if (row.getCell(i).getCellType() == 1) {
                invalids.add(new ValidationExceptionDetails(GeneralValidationErrorType.DATA_MISSING, "{"+String.valueOf(row.getRowNum() + 1)+","+(i+1)+"}", "数据错误"));
                break;  // 如果当前单元格错误,还需保存其他单元格的 就使用 conticu.
            }
            if (row.getCell(i) == null || row.getCell(i).getCellType() == 3) {
                continue;
            } //如果利率为空 不保存当前这条报价.
            if (isEligibility(row.getCell(i).getNumericCellValue()) != null) {
                price = isEligibility(row.getCell(i).getNumericCellValue()).setScale(FOUR, BigDecimal.ROUND_HALF_UP);
                mmQuoteDetailsDto.setPrice(price); //利率
                if (i <= 11) {
                    mmQuoteDetailsDto.setLimitType(getQuoteTimePeriod(i));  //设置 期限
                } else {
                    sheet.getRow(2).getCell(i).setCellType(HSSFCell.CELL_TYPE_STRING); //将单元格设置为string 类型
                    String customTerm = sheet.getRow(2).getCell(i).getStringCellValue().replaceAll(" ", "");
                    getCustomTerm(customTerm, mmQuoteDetailsDto);  //设置自定义期限
                    if (mmQuoteDetailsDto.getDayHigh() == null && mmQuoteDetailsDto.getDayLow() == null) {
                        invalids.add(new ValidationExceptionDetails(GeneralValidationErrorType.DATA_MISSING, "{"+String.valueOf(3)+","+(i+1)+"}", "数据错误"));
                    }
                }
            } else {
                invalids.add(new ValidationExceptionDetails(GeneralValidationErrorType.DATA_MISSING, "{"+String.valueOf(row.getRowNum() + 1)+","+(i+1)+"}", "数据错误"));
            }
            results.add(mmQuoteDetailsDto);
        }
    }

    private BigDecimal isEligibility(double value) {
        if (value >= -100 && value <= 100) {
            return new BigDecimal(value);
        }
        return null;
    }

    //excel单元格对应的天数期限
    private QuoteTimePeriod getQuoteTimePeriod(int i) {
        switch (i) {
            case 4:
                return QuoteTimePeriod.T7D;
            case 5:
                return QuoteTimePeriod.T14D;
            case 6:
                return QuoteTimePeriod.T1M;
            case 7:
                return QuoteTimePeriod.T2M;
            case 8:
                return QuoteTimePeriod.T3M;
            case 9:
                return QuoteTimePeriod.T6M;
            case 10:
                return QuoteTimePeriod.T9M;
            case 11:
                return QuoteTimePeriod.T1Y;
        }
        return null;
    }

    private static void getCustomTerm(String days, MmQuoteDetailsDto mmQuoteDetailsDto) {

        if (days.matches("\\d+D")) {
            getNoIntervalDays(days, D, mmQuoteDetailsDto);
        }

        if (days.matches("\\d+M")) {
            getNoIntervalDays(days, M, mmQuoteDetailsDto);
        }

        if (days.matches("\\d+Y")) {
            getNoIntervalDays(days, Y, mmQuoteDetailsDto);
        }

        if (days.matches("\\d+天")) {
            getNoIntervalDays(days, DD, mmQuoteDetailsDto);
        }

        if (days.matches("\\d+个月")) {
            getNoIntervalDays(days, MM, mmQuoteDetailsDto);
        }

        if (days.matches("\\d+年")) {
            getNoIntervalDays(days, YY, mmQuoteDetailsDto);
        }

    }


    //自定义无区间报价
    private static void getNoIntervalDays(String days, String format, MmQuoteDetailsDto mmQuoteDetailsDto) {
        if (format.equals(D)) {
            mmQuoteDetailsDto.setDayLow(Integer.valueOf(days.substring(0, days.indexOf("D"))));
            mmQuoteDetailsDto.setDayHigh(mmQuoteDetailsDto.getDayLow());
        }
        if (format.equals(DD)) {
            mmQuoteDetailsDto.setDayLow(Integer.valueOf(days.substring(0, days.indexOf("天"))));
            mmQuoteDetailsDto.setDayHigh(mmQuoteDetailsDto.getDayLow());
        }
        if (format.equals(M)) {
            mmQuoteDetailsDto.setDayLow(Integer.valueOf(days.substring(0, days.indexOf("M"))) * 30);  //转为天数
            mmQuoteDetailsDto.setDayHigh(mmQuoteDetailsDto.getDayLow());
        }
        if (format.equals(MM)) {
            mmQuoteDetailsDto.setDayLow(Integer.valueOf(days.substring(0, days.indexOf("个月"))) * 30);  //转为天数
            mmQuoteDetailsDto.setDayHigh(mmQuoteDetailsDto.getDayLow());
        }
        if (format.equals(Y)) {
            mmQuoteDetailsDto.setDayLow(Integer.valueOf(days.substring(0, days.indexOf("Y"))) * 360);
            mmQuoteDetailsDto.setDayHigh(mmQuoteDetailsDto.getDayLow());
        }
        if (format.equals(YY)) {
            mmQuoteDetailsDto.setDayLow(Integer.valueOf(days.substring(0, days.indexOf("年"))) * 360);
            mmQuoteDetailsDto.setDayHigh(mmQuoteDetailsDto.getDayLow());
        }
    }

    //生成对应版本的excel的工作簿对象
    private static Workbook createWorkbook(InputStream inp) {
        try {
            if (!inp.markSupported()) {
                inp = new PushbackInputStream(inp, 8);
            }
            if (POIFSFileSystem.hasPOIFSHeader(inp)) {
                return new HSSFWorkbook(inp);
            }
            if (POIXMLDocument.hasOOXMLHeader(inp)) {
                return new XSSFWorkbook(OPCPackage.open(inp));
            }
        } catch (IOException e) {
            throw new BusinessRuntimeException(MMExceptionCode.EXCEL_ERROR, "您上传的文件出现了无法解析的错误!!!");
        } catch (InvalidFormatException e) {
            throw new BusinessRuntimeException(MMExceptionCode.EXCEL_ERROR, e);
        }
        throw new BusinessRuntimeException(MMExceptionCode.EXCEL_ERROR, "您上传的文件出现了无法解析的错误!!!");
    }
}