package com.aidl.aidl.print;


import android.os.Parcel;
import android.os.Parcelable;

public class DcPrintItemObj implements Parcelable{
	// 对齐枚举
	public static enum ALIGN {
		LEFT, CENTER, RIGHT
	}

	private String text = null;
	private int fontSize = 8;
	private boolean isBold = false;
	public  ALIGN align = ALIGN.LEFT;
	private boolean isUnderline = false;
	private boolean isWordWrap = true;
	private int lineHeight = 29;
	private int letterSpacing = 0x00;
	private int marginLeft = 0x00;

	// 构造方法
	public DcPrintItemObj(Parcel source) {
		this.text = source.readString();
		this.fontSize = source.readInt();
		this.isBold = (Boolean) source
				.readValue(Boolean.class.getClassLoader());
		this.align = (ALIGN) source.readValue(ALIGN.class.getClassLoader());
		this.isUnderline = (Boolean) source.readValue(Boolean.class
				.getClassLoader());
		this.isWordWrap = (Boolean) source.readValue(Boolean.class
				.getClassLoader());
		this.lineHeight = source.readInt();
		this.letterSpacing = source.readInt();
		this.marginLeft = source.readInt();
	}

	public DcPrintItemObj(String text, int fontSize, boolean isBold,
			ALIGN align, boolean isUnderline, boolean isWordWrap,
			int lineHeight, int letterSpacing, int marginLeft) {
		super();
		this.text = text;
		this.fontSize = fontSize;
		this.isBold = isBold;
		this.align = align;
		this.isUnderline = isUnderline;
		this.isWordWrap = isWordWrap;
		if(lineHeight < 26){
			lineHeight = 26;
		}else{
			this.lineHeight = lineHeight;
		}
		this.letterSpacing = letterSpacing;
		this.marginLeft = marginLeft;
	}

	public DcPrintItemObj(String text, int fontSize, boolean isBold,
			ALIGN align, boolean isUnderline, boolean isWordWrap,
			int lineHeight, int letterSpacing) {
		super();
		this.text = text;
		this.fontSize = fontSize;
		this.isBold = isBold;
		this.align = align;
		this.isUnderline = isUnderline;
		this.isWordWrap = isWordWrap;
		this.lineHeight = lineHeight;
		this.letterSpacing = letterSpacing;
	}

	public DcPrintItemObj(String text, int fontSize, boolean isBold,
			ALIGN align, boolean isUnderline, boolean isWordWrap, int lineHeight) {
		super();
		this.text = text;
		this.fontSize = fontSize;
		this.isBold = isBold;
		this.align = align;
		this.isUnderline = isUnderline;
		this.isWordWrap = isWordWrap;
		this.lineHeight = lineHeight;
	}

	public DcPrintItemObj(String text, int fontSize, boolean isBold,
			ALIGN align, boolean isUnderline, boolean isWordWrap) {
		super();
		this.text = text;
		this.fontSize = fontSize;
		this.isBold = isBold;
		this.align = align;
		this.isUnderline = isUnderline;
		this.isWordWrap = isWordWrap;
	}

	public DcPrintItemObj(String text, int fontSize, boolean isBold,
			ALIGN align, boolean isUnderline) {
		super();
		this.text = text;
		this.fontSize = fontSize;
		this.isBold = isBold;
		this.align = align;
		this.isUnderline = isUnderline;
	}

	public DcPrintItemObj(String text, int fontSize, boolean isBold,
			ALIGN align) {
		super();
		this.text = text;
		this.fontSize = fontSize;
		this.isBold = isBold;
		this.align = align;
	}

	public DcPrintItemObj(String text, int fontSize, boolean isBold) {
		super();
		this.text = text;
		this.fontSize = fontSize;
		this.isBold = isBold;
	}

	public DcPrintItemObj(String text, int fontSize) {
		super();
		this.text = text;
		this.fontSize = fontSize;
	}

	public DcPrintItemObj(String text) {
		super();
		this.text = text;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		 dest.writeString(text);
		 dest.writeInt(fontSize);
		 dest.writeValue(isBold);
		 dest.writeValue(align);
		 dest.writeValue(isUnderline);
		 dest.writeValue(isWordWrap);
		 dest.writeInt(lineHeight);
		 dest.writeInt(letterSpacing);
		 dest.writeInt(marginLeft);
	}

	public static final Creator<DcPrintItemObj> CREATOR = new Creator<DcPrintItemObj>() {

		@Override
		public DcPrintItemObj createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			DcPrintItemObj printObj = new DcPrintItemObj(source);
			return printObj;
		}

		@Override
		public DcPrintItemObj[] newArray(int size) {
			// TODO Auto-generated method stub
			return null;
		}
	};

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public boolean isBold() {
		return isBold;
	}

	public void setBold(boolean isBold) {
		this.isBold = isBold;
	}

	public ALIGN getAlign() {
		return align;
	}

	public void setAlign(ALIGN align) {
		this.align = align;
	}

	public boolean isUnderline() {
		return isUnderline;
	}

	public void setUnderline(boolean isUnderline) {
		this.isUnderline = isUnderline;
	}

	public boolean isWordWrap() {
		return isWordWrap;
	}

	public void setWordWrap(boolean isWordWrap) {
		this.isWordWrap = isWordWrap;
	}

	public int getLineHeight() {
		return lineHeight;
	}

	public void setLineHeight(int lineHeight) {
		if(lineHeight < 26){
			this.lineHeight = 26;
		}else{
			this.lineHeight = lineHeight;
		}
	}

	public int getLetterSpacing() {
		return letterSpacing;
	}

	public void setLetterSpacing(int letterSpacing) {
		this.letterSpacing = letterSpacing;
	}

	public int getMarginLeft() {
		return marginLeft;
	}

	public void setMarginLeft(int marginLeft) {
		this.marginLeft = marginLeft;
	}
	
	
}
