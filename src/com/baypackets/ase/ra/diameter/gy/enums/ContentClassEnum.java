package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumContentClass;

public enum ContentClassEnum
{
CONTENTBASIC,
CONTENTRICH,
IMAGEBASIC,
IMAGERICH,
MEGAPIXEL,
TEXT,
VIDEOBASIC,
VIDEORICH;

private static Hashtable<ContentClassEnum,EnumContentClass> stackMapping = new Hashtable<ContentClassEnum,EnumContentClass>();
private static Hashtable<EnumContentClass,ContentClassEnum> containerMapping = new Hashtable<EnumContentClass,ContentClassEnum>();

 static {
stackMapping.put(ContentClassEnum.CONTENTBASIC, EnumContentClass.ContentBasic);
stackMapping.put(ContentClassEnum.CONTENTRICH, EnumContentClass.ContentRich);
stackMapping.put(ContentClassEnum.IMAGEBASIC, EnumContentClass.ImageBasic);
stackMapping.put(ContentClassEnum.IMAGERICH, EnumContentClass.ImageRich);
stackMapping.put(ContentClassEnum.MEGAPIXEL, EnumContentClass.Megapixel);
stackMapping.put(ContentClassEnum.TEXT, EnumContentClass.Text);
stackMapping.put(ContentClassEnum.VIDEOBASIC, EnumContentClass.VideoBasic);
stackMapping.put(ContentClassEnum.VIDEORICH, EnumContentClass.VideoRich);

containerMapping.put(EnumContentClass.ContentBasic, ContentClassEnum.CONTENTBASIC);
containerMapping.put(EnumContentClass.ContentRich, ContentClassEnum.CONTENTRICH);
containerMapping.put(EnumContentClass.ImageBasic, ContentClassEnum.IMAGEBASIC);
containerMapping.put(EnumContentClass.ImageRich, ContentClassEnum.IMAGERICH);
containerMapping.put(EnumContentClass.Megapixel, ContentClassEnum.MEGAPIXEL);
containerMapping.put(EnumContentClass.Text, ContentClassEnum.TEXT);
containerMapping.put(EnumContentClass.VideoBasic, ContentClassEnum.VIDEOBASIC);
containerMapping.put(EnumContentClass.VideoRich, ContentClassEnum.VIDEORICH);
}

public static final ContentClassEnum getContainerObj(EnumContentClass stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumContentClass getStackObj(ContentClassEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static ContentClassEnum fromCode(int value){
	return getContainerObj(EnumContentClass.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumContentClass.getName(key);
}

public static boolean isValid(int value){
	return EnumContentClass.isValid(value);
}

public static int[] keys(){
	return EnumContentClass.keys();
}
}
