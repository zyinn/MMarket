package com.sumscope.optimus.moneymarket.model.dto;

import com.sumscope.optimus.moneymarket.commons.enums.Direction;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteType;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteTimePeriod;
import java.math.BigDecimal;
import java.util.List;

/**
 * 手机理财应用程序搜索报价单的参数Dto
 * 
 */
public class MobileQueryQuoteMainParameterDto {

	/**
	 * 报价方向
	 * 
	 */
	private Direction direction;

	/**
	 * 报价类型
	 * 
	 */
	private QuoteType quoteType;

	/**
	 * 用于排序的期限，可能为空，
	 * 不为空时，若方向是出则降序排序
	 * 若方向是入则升序排序
	 * 当为空时按报价日期降序排序
	 */
	private QuoteTimePeriod sortByPeriod;

	/**
	 * 机构规模低值，为空时表明不限制低值
	 */
	private BigDecimal institutionScaleLow;

	/**
	 * 机构规模高值，为空时表明不限制高值
	 */
	private BigDecimal institutionScaleHigh;

	/**
	 * 省份列表，当空时表示查询全部省份
	 */
	private List<String> areas;

	private String areasSign;

	private Integer qdetailsDayHigh;
	//自定义报价时,期限在这两字段设置.
	private Integer qdetailsDayLow;

	/**
	 * 是否按机构名称的拼音进行排序
	 */
	private boolean sortByInstitutionPY = false;

	/**
	 * 产品特征标签列表
	 */
	private List<String> tagCodes;
	/**
	 * 是否分页
	 */
	private boolean page=false;
	/**
	 * 从第几行开始
	 */
	private long pageNumber;
	/**
	 * 页容量
	 */
	private int pageSize;

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public QuoteType getQuoteType() {
		return quoteType;
	}

	public void setQuoteType(QuoteType quoteType) {
		this.quoteType = quoteType;
	}

	public QuoteTimePeriod getSortByPeriod() {
		return sortByPeriod;
	}

	public void setSortByPeriod(QuoteTimePeriod sortByPeriod) {
		this.sortByPeriod = sortByPeriod;
	}

	public BigDecimal getInstitutionScaleLow() {
		return institutionScaleLow;
	}

	public void setInstitutionScaleLow(BigDecimal institutionScaleLow) {
		this.institutionScaleLow = institutionScaleLow;
	}

	public BigDecimal getInstitutionScaleHigh() {
		return institutionScaleHigh;
	}

	public void setInstitutionScaleHigh(BigDecimal institutionScaleHigh) {
		this.institutionScaleHigh = institutionScaleHigh;
	}

	public List<String> getAreas() {
		return areas;
	}

	public void setAreas(List<String> areas) {
		this.areas = areas;
	}

	public String getAreasSign() {
		List<String> areas = getAreas();
		if(areas==null||areas.size()==0){
			return null;
		}else{
			areasSign = "OK";
			return areasSign;
		}
	}

	public void setAreasSign(String areasSign) {
		this.areasSign = areasSign;
	}

	public Integer getQdetailsDayHigh() {
		return qdetailsDayHigh;
	}

	public void setQdetailsDayHigh(Integer qdetailsDayHigh) {
		this.qdetailsDayHigh = qdetailsDayHigh;
	}

	public Integer getQdetailsDayLow() {
		return qdetailsDayLow;
	}

	public void setQdetailsDayLow(Integer qdetailsDayLow) {
		this.qdetailsDayLow = qdetailsDayLow;
	}

	public boolean isSortByInstitutionPY() {
		return sortByInstitutionPY;
	}

	public void setSortByInstitutionPY(boolean sortByInstitutionPY) {
		this.sortByInstitutionPY = sortByInstitutionPY;
	}

	/**
	 * 由于mybatis中的if test 无法直接使用枚举，因此使用该getter来达到枚举判断的目的。
	 */
	public String getOrderSeq() {
		if(direction == Direction.OUT){
			return "DESC";
		}else{
			return "ASC";
		}
	}

	public List<String> getTagCodes() {
		return tagCodes;
	}

	public void setTagCodes(List<String> tagCodes) {
		this.tagCodes = tagCodes;
	}

	public long getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(long pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public boolean isPage() {
		return page;
	}

	public void setPage(boolean page) {
		this.page = page;
	}
}
