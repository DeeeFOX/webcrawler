package cn.scut.chiu.webcrawler.crawler;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;

import cn.scut.chiu.webcrawler.conf.Params;
import cn.scut.chiu.webcrawler.crawler.object.HTMLNode;

public class TextDetector {

	/**
	 * core algorithm of the webcrawler, detect main text part from a static web page
	 * by use of chinese punctiation,anchor text
	 *
	 * @param parent
	 * @param parentChiuScore
	 * @param parentNonHrefRate
	 * @param stage
	 * @return
	 */
	public static Element detectNewsMainText(Element parent, double parentChiuScore, double parentNonHrefRate, int stage) {
//		System.out.println("------ start filtering none text tag stage " + stage + "------");
		
		if(parentChiuScore == 0.0 || parentNonHrefRate == 0.0) {
			// when first in, parent's chiu score is cal by this node
			HTMLNode parentNode = new HTMLNode(parent);
			double parentNonHrefLength = parentNode.getTextLength(false, false, false);
			double parentTextLength = parentNode.getTextLength(false, false, true);
			parentNonHrefRate = parentNonHrefLength / parentTextLength;
			//
			parentChiuScore = parentNonHrefRate * parentNonHrefLength * Math.log10(parentNode.getTextPuncScore());
		}
		
		List<Element> children = new ArrayList<Element>(parent.children().size());
		// get all the children node
		for(Element child : parent.children()) {
			children.add(child);
		}
		// init children's scores
		double[] childPuncScore = new double[children.size()];
		double maxPuncScore = 0.0;
		int maxPuncScoreIndex = -1;
		int[] childNonHrefTextLength = new int[children.size()];
		double[] childNonHrefTextRate = new double[children.size()];
		double[] childChiuScore = new double[children.size()];
		double maxChiuScore = -Double.MAX_VALUE;
		int maxChiuScoreIndex = -1;
		double secondMaxChiuScore = -Double.MAX_VALUE + 1;
		int secondMaxChiuScoreIndex = -1;
		
		for(int i=0; i<children.size(); i++) {
			// get children node and count
			HTMLNode node = new HTMLNode(children.get(i));
			
			// once stage 1 count, now no matter what it is count!
			// count the punctuation score
			childPuncScore[i] = node.getTextPuncScore();
			if(maxPuncScore < childPuncScore[i]) {
				maxPuncScore = childPuncScore[i];
				maxPuncScoreIndex = i;
			}
			
			// count child's non href score
			if(node.getTextLength(false, false, true) == 0) {
				// now that no text length, nonHrefTextLength
				childNonHrefTextLength[i] = -1;
				childNonHrefTextRate[i] = -1.0;
				childChiuScore[i] = -1.0;
			} else {
				// count the non href text score
				childNonHrefTextLength[i] = node.getTextLength(false, false, false);
				childNonHrefTextRate[i] = (double)childNonHrefTextLength[i] / (double)node.getTextLength(false, false, true);
				// the non href text score, childPuncScore in order to limit the articles with words too few, other part too more
				if(childPuncScore[i] == 0) {
					childChiuScore[i] = childNonHrefTextLength[i] * childNonHrefTextRate[i] * childPuncScore[i];
				} else {
					childChiuScore[i] = childNonHrefTextLength[i] * childNonHrefTextRate[i] * Math.log10(childPuncScore[i]);
				}
			}
			
			if(maxChiuScore < childChiuScore[i]) {
				secondMaxChiuScore = maxChiuScore;
				maxChiuScore = childChiuScore[i];
				secondMaxChiuScoreIndex = maxChiuScoreIndex;
				maxChiuScoreIndex = i;
			} else {
				if(secondMaxChiuScore < childChiuScore[i]) {
					secondMaxChiuScore = childChiuScore[i];
					secondMaxChiuScoreIndex = i;
				}
			}
			
//			System.out.print("nonHref length: " + childNonHrefTextLength[i]);
//			System.out.print("  nonHref rate: " + childNonHrefTextRate[i]);
//			System.out.print("  punc score: " + childPuncScore[i]);
//			System.out.println("  chiu score: " + childChiuScore[i]);
//			System.out.print("nonHref inc rate: " + childNonHrefTextRate[maxChiuScoreIndex]/parentNonHrefRate);
//			System.out.print("  chiu dec rate: " + (childChiuScore[i] / parentChiuScore));
//			System.out.println("  second/max: " + (secondMaxChiuScore/maxChiuScore));
//			System.out.println("text: " + children.get(i).text() + '\n');
		}
//		System.out.println(maxPuncScore);
		if (stage == 1) {
			// When in stage 1 stage one consider punc score first
			
			if(maxPuncScoreIndex < 0 || parentChiuScore * Params.MIN_CHIU_SCORE_DECLINE_RATE_STAGE_1_BOTTOM > Math.abs(maxChiuScore)) {
				// no maxPuncScoreIndex indicates that no punctuation in every child node or in this parent node
				// or
				// chiu score decline too much which indicates that parent has much more punctuation score(parent should be the chosen one)
				if(maxPuncScoreIndex >= 0 && childNonHrefTextRate[maxPuncScoreIndex] < Params.MAX_CHILD_NON_HREF_TEXT_RATE) {
					// there is a max punctuation score node index
					// and
					// chiu scoure decline much
					// parent punc score is low
					// non href rate is low
					// then count href rate more in detail(go into stage 2)
					return detectNewsMainText(parent, parentChiuScore, parentNonHrefRate, 2);
				} else {
					// there is no great punctuation score index(maybe impossible?)
					// or
					// there is great non score in parent
					// and
					// non href rate is high
					// then is you! the parent!
//					return parent;
					if(Math.abs(secondMaxChiuScore/maxChiuScore) < Params.SECONDE_MAX_CHIU_SCROE_TO_MAX_CHIU_SCORE_RATE && parentChiuScore*Params.MAX_CHIU_SCORE_DECLINE_RATE_STAGE_1 < Math.abs(maxChiuScore)) {
						return children.get(maxChiuScoreIndex);
					} else {
						return parent;
					}
				}
			} else {
				// there is greate punc score in child
				// and
				// parent has almost the same punctuation score as its child
				return detectNewsMainText(children.get(maxChiuScoreIndex), maxChiuScore, childNonHrefTextRate[maxChiuScoreIndex], stage);
			}
		} else {

			// In href stage
			if(maxChiuScoreIndex < 0 || (parentChiuScore*Params.MIN_CHIU_SCORE_DECLINE_RATE_STAGE_2_BOTTOM > Math.abs(maxChiuScore) && childNonHrefTextRate[maxChiuScoreIndex] >= Params.MAX_CHILD_NON_HREF_TEXT_RATE_2)) {
				// when this happen, it means
				// program has into a parent node that with out any text
				// and here should not return parent too!
				// so this situation may never happen!?
				return parent;
			}
			// 
			if(childNonHrefTextRate[maxChiuScoreIndex] >= Params.MAX_CHILD_NON_HREF_TEXT_RATE) {
				if((parentChiuScore*Params.MIN_CHIU_SCORE_DECLINE_RATE_STAGE_2_ROOF > Math.abs(maxChiuScore)) || secondMaxChiuScoreIndex < 0 || childNonHrefTextRate[maxChiuScoreIndex]/parentNonHrefRate > Params.MAX_NON_HREF_RATE_INCR_STAGE_2) {
					return children.get(maxChiuScoreIndex);
				}
				// In this situation there must be no puncScore in this short content
				return children.get(secondMaxChiuScoreIndex);
			} else {
				// here may have another situation that:
				// think more carefully!
				return detectNewsMainText(children.get(maxChiuScoreIndex), childChiuScore[maxChiuScoreIndex], childNonHrefTextRate[maxChiuScoreIndex], stage);
			}
		}
	}

	public static Element detectBlogMainText(Element parent, double parentChiuScore, double parentNonHrefRate, int stage) {
//		System.out.println("------ start filtering none text tag stage " + stage + "------");

		if(parentChiuScore == 0.0 || parentNonHrefRate == 0.0) {
			// when first in, parent's chiu score is cal by this node
			HTMLNode parentNode = new HTMLNode(parent);
			double parentNonHrefLength = parentNode.getTextLength(false, false, false);
			double parentTextLength = parentNode.getTextLength(false, false, true);
			parentNonHrefRate = parentNonHrefLength / parentTextLength;
			//
			parentChiuScore = parentNonHrefRate * parentNonHrefLength * Math.log10(parentNode.getTextPuncScore());
		}

		List<Element> children = new ArrayList<Element>(parent.children().size());
		// get all the children node
		for(Element child : parent.children()) {
			children.add(child);
		}
		// init children's scores
		double[] childPuncScore = new double[children.size()];
		double maxPuncScore = 0.0;
		int maxPuncScoreIndex = -1;
		int[] childNonHrefTextLength = new int[children.size()];
		double[] childNonHrefTextRate = new double[children.size()];
		double[] childChiuScore = new double[children.size()];
		double maxChiuScore = -Double.MAX_VALUE;
		int maxChiuScoreIndex = -1;
		double secondMaxChiuScore = -Double.MAX_VALUE + 1;
		int secondMaxChiuScoreIndex = -1;

		for(int i=0; i<children.size(); i++) {
			// get children node and count
			HTMLNode node = new HTMLNode(children.get(i));

			// once stage 1 count, now no matter what it is count!
			// count the punctuation score
			childPuncScore[i] = node.getTextPuncScore();
			if(maxPuncScore < childPuncScore[i]) {
				maxPuncScore = childPuncScore[i];
				maxPuncScoreIndex = i;
			}

			// count child's non href score
			if(node.getTextLength(false, false, true) == 0) {
				// now that no text length, nonHrefTextLength
				childNonHrefTextLength[i] = -1;
				childNonHrefTextRate[i] = -1.0;
				childChiuScore[i] = -1.0;
			} else {
				// count the non href text score
				childNonHrefTextLength[i] = node.getTextLength(false, false, false);
				childNonHrefTextRate[i] = (double)childNonHrefTextLength[i] / (double)node.getTextLength(false, false, true);
				// the non href text score, childPuncScore in order to limit the articles with words too few, other part too more
				if(childPuncScore[i] == 0) {
					childChiuScore[i] = childNonHrefTextLength[i] * childNonHrefTextRate[i] * childPuncScore[i];
				} else {
					childChiuScore[i] = childNonHrefTextLength[i] * childNonHrefTextRate[i] * Math.log10(childPuncScore[i]);
				}
			}

			if(maxChiuScore < childChiuScore[i]) {
				secondMaxChiuScore = maxChiuScore;
				maxChiuScore = childChiuScore[i];
				secondMaxChiuScoreIndex = maxChiuScoreIndex;
				maxChiuScoreIndex = i;
			} else {
				if(secondMaxChiuScore < childChiuScore[i]) {
					secondMaxChiuScore = childChiuScore[i];
					secondMaxChiuScoreIndex = i;
				}
			}
		}
//		System.out.println(maxPuncScore);
		if (stage == 1) {
			// When in stage 1 stage one consider punc score first

			if(Params.MIN_CHIU_SCORE_DECLINE_RATE_STAGE_1_BOTTOM > Math.abs(maxChiuScore) / parentChiuScore) {
				// no maxPuncScoreIndex indicates that no punctuation in every child node or in this parent node
				// or
				// chiu score decline too much which indicates that parent has much more punctuation score(parent should be the chosen one)
				if(childNonHrefTextRate[maxPuncScoreIndex] < Params.MAX_CHILD_NON_HREF_TEXT_RATE && Params.MIN_CHIU_SCORE_DECLINE_RATE_STAGE_1_BOTTOM > Math.abs(maxChiuScore) / parentChiuScore) {
					// there is a max punctuation score node index
					// and
					// chiu scoure decline much
					// parent punc score is low
					// non href rate is low
					// then count href rate more in detail(go into stage 2)
					return detectNewsMainText(parent, parentChiuScore, parentNonHrefRate, 2);
				} else {
					// there is no great punctuation score index(maybe impossible?)
					// or
					// there is great non score in parent
					// and
					// non href rate is high
					// then is you! the parent!
//					return parent;
					if(Math.abs(secondMaxChiuScore/maxChiuScore) < Params.SECONDE_MAX_CHIU_SCROE_TO_MAX_CHIU_SCORE_RATE && Params.MAX_CHIU_SCORE_DECLINE_RATE_STAGE_1 < Math.abs(maxChiuScore) / parentChiuScore) {
						return children.get(maxChiuScoreIndex);
					} else {
						return parent;
					}
				}
			} else {
				// there is greate punc score in child
				// and
				// parent has almost the same punctuation score as its child
				return detectNewsMainText(children.get(maxChiuScoreIndex), maxChiuScore, childNonHrefTextRate[maxChiuScoreIndex], stage);
			}
		} else {

			// In href stage
			if(Params.MIN_CHIU_SCORE_DECLINE_RATE_STAGE_2_BOTTOM > Math.abs(maxChiuScore)/parentChiuScore && childNonHrefTextRate[maxChiuScoreIndex] >= Params.MAX_CHILD_NON_HREF_TEXT_RATE_2) {
				// when this happen, it means
				// program has into a parent node that with out any text
				// and here should not return parent too!
				// so this situation may never happen!?
				return parent;
			}
			//
			if(childNonHrefTextRate[maxChiuScoreIndex] >= Params.MAX_CHILD_NON_HREF_TEXT_RATE) {
				if((parentChiuScore*Params.MIN_CHIU_SCORE_DECLINE_RATE_STAGE_2_ROOF > Math.abs(maxChiuScore)) || secondMaxChiuScoreIndex < 0 || childNonHrefTextRate[maxChiuScoreIndex]/parentNonHrefRate > Params.MAX_NON_HREF_RATE_INCR_STAGE_2) {
					return children.get(maxChiuScoreIndex);
				}
				// In this situation there must be no puncScore in this short content
				return children.get(secondMaxChiuScoreIndex);
			} else {
				// here may have another situation that:
				// think more carefully!
				return detectNewsMainText(children.get(maxChiuScoreIndex), childChiuScore[maxChiuScoreIndex], childNonHrefTextRate[maxChiuScoreIndex], stage);
			}
		}
	}
}
