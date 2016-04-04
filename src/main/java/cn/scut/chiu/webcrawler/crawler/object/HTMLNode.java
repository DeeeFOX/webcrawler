package cn.scut.chiu.webcrawler.crawler.object;

import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * HTMLNode is represent a html page's node structure with many method to deal with
 * 
 * @author Chiu
 *
 */
public class HTMLNode {

	private Element ele = null;
	
	private List<Element> children = null;
	
	private List<Element> remainChildren = null;
	
	public HTMLNode(Element ele) {
		this.ele = ele;
		this.children = ele.children();
	}
	
	/**
	 * 
	 * @param isChineseOnly if return chinese word's length only
	 * @param isContainEng if return english word length only
	 * @param isContainHrefText if return word's length including anchor text
	 * @return
	 */
	public int getTextLength(boolean isChineseOnly, boolean isContainEng, boolean isContainHrefText) {
		String text = this.getText();
		int textNoiseLength = 0;
		int hrefTextLength = 0;
		if(isChineseOnly) {
			textNoiseLength = text.replaceAll("[\u4e00-\uafa5]", "").length();
		} else {
			if(!isContainEng) {
				textNoiseLength = text.length() - text.replaceAll("[A-Z|a-z]", "").length();
			}
		}
		if(!isContainHrefText) {
			Elements hrefs = this.ele.getElementsByTag("a");
			for(Element href : hrefs) {
				hrefTextLength += new HTMLNode(href).getTextLength(isChineseOnly, isContainEng, true);
			}
		}
		return text.length() - textNoiseLength - hrefTextLength;
		
	}
	
	public String getText() {
		return this.ele.text().replaceAll("&nbsp", " ").replaceAll("\u00a0", " ")
				.replaceAll("\n", " ").replaceAll("\r", " ").replaceAll("\\s", " ").trim(); // \u00a0��&nbsp;��Unicode��
	}
	
	/**
	 * 
	 * @return at least 0
	 */
	public double getTextPuncScore() {
		String text = this.getText().replaceAll("<li>(?:.|[\r\n]*).*</li>", "");
//		System.out.print("text: " + text);
		String noiseText_noComma = text.replaceAll("[\u002C]|[\uFF0C]", "");
		String noiseText_noPeriod = text.replaceAll("[\u002E]|[\uFF0E]|[\uFE12]|[\uFF61]", ""); // \u002E-- ?.
		String noiseText_noCPeriod = text.replaceAll("[\u3002]", ""); // \u3002 chinese period
		String noiseText_noIdeographicComma = text.replaceAll("[\u3001]|[\uFE51]|[\uFF64]", "");
		String noiseText_noSemicolon = text.replaceAll("[\u003B]|[\uFF1B]|[\uFE14]|[\uFE54]|[\u037E]", "");
		String noiseText_noQuatation = text.replaceAll("[\u2018]|[\u2019]|[\u201A]|[\u201B]|[\u201C]|[\u201D]|[\u201E]|[\u201F]", "");
		String noiseText_noLess = text.replaceAll("[\u003C]", ""); // \u003C -- <
		String noiseText_noEq = text.replaceAll("[\u003D]", "");
//		String noiseText_noBookTitleMark = text.replaceAll("[\u300A]|[\u300B]", "");
		int countComma = text.length() - noiseText_noComma.length();
		double scoreComma = countComma*countComma;
		int countPeriod = text.length() - noiseText_noPeriod.length();
		double scorePeriod = countPeriod*countPeriod;
		int countIdeographicComma = text.length() - noiseText_noIdeographicComma.length();
		double scoreIdeographicComma = countIdeographicComma*countIdeographicComma;
		int countSemicolon = text.length() - noiseText_noSemicolon.length();
		double scoreSemicolon = countSemicolon*countSemicolon;
		int countQuatation = text.length() - noiseText_noQuatation.length();
		double scoreQuatation = countQuatation*countQuatation;
		int countCPeriod = text.length() - noiseText_noCPeriod.length();
		double scoreCPeriod = countCPeriod*countCPeriod*countCPeriod;
		int countLess = text.length() - noiseText_noLess.length();
		double negScoreSlash = - Math.pow(countLess, 2);
		int countEq = text.length() - noiseText_noEq.length();
		double negScoreEq = - Math.pow(countEq, 1.55);
//		int countBookTitleMark = text.length() - noiseText_noBookTitleMark.length();
//		long scoreBookTitleMark = countBookTitleMark*countBookTitleMark;
		return scoreComma + scorePeriod + scoreIdeographicComma + scoreSemicolon + scoreQuatation + scoreCPeriod + negScoreSlash + negScoreEq;
	}
	
	public void setRemainChildren(List<Element> children) {
		this.remainChildren = children;
	}
	
	public List<Element> getRemainChildren() {
		return this.remainChildren;
	}
	
	public List<Element> getChildren() {
		return this.children;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method 
		String text = "<li>fuck\nfuck</li>";
		System.out.println(text.replaceAll("<li>(?:.|[\r\n])*</li>", ""));
	}

	
}
