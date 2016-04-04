package cn.scut.chiu.webcrawler.crawler.object;

public abstract class TextInfo {

	protected int infoType;
	protected String saveKey;
	protected String title;
	protected String mainText;
	protected int category;
	protected int textType;
	protected long createTime;
	protected double polarity;
	
	public abstract void init();
	
	public String getSaveKey() {
		return saveKey;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isTitleInvalid() {
		return title == null || title.trim().equals("");
	}
	
	public String getMainText() {
		return mainText;
	}

	public void setMainText(String mainText) {
		this.mainText = mainText;
	}
	
	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public void setPolarity(double polar) {
		this.polarity = polar;
	}
	
	public double getPolarity() {
		return polarity;
	}
	
	public int getInfoType() {
		return infoType;
	}
	
	public int getTextType() {
		return textType;
	}

	public void setTextType(int textType) {
		this.textType = textType;
	}
	
	public void initFromDB(String saveKey) {
		// fuck that from db
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public abstract void printXML();
	public void setSaveKey(String saveKey) {
		this.saveKey = saveKey;
	}
}
