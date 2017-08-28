package com.aidl.utils;

import java.io.ByteArrayOutputStream;

public class EMVTAGStr {
    public static final String EMVTAG_APP_PAN = combine(0x5A);
    public static final String EMVTAG_APP_PAN_SN = combine(0x5F,0x34);
    public static final String EMVTAG_TRACK2 = combine(0x57);
    public static final String EMVTAG_AC = combine(0x9F,0x26);
    public static final String EMVTAG_CID = combine(0x9F,0x27);
    public static final String EMVTAG_IAD  = combine(0x9F,0x10);        //Issuer Application Data
    public static final String EMVTAG_RND_NUM  = combine(0x9F,0x37);        //Random Number
    public static final String EMVTAG_ATC  = combine(0x9F,0x36);
    public static final String EMVTAG_TVR  = combine(0x95);
    public static final String EMVTAG_TXN_DATE  = combine(0x9A);
    public static final String EMVTAG_TXN_TYPE  = combine(0x9C);
    public static final String EMVTAG_AMOUNT = combine(0x9F,0x02);
    public static final String EMVTAG_CURRENCY  = combine(0x5F,0x2A);
    public static final String EMVTAG_AIP  = combine(0x82);
    public static final String EMVTAG_COUNTRY_CODE  = combine(0x9F,0x1A);
    public static final String EMVTAG_OTHER_AMOUNT  = combine(0x9F,0x03);
    public static final String EMVTAG_TERM_CAP    = combine(0x9F,0x33);
    public static final String EMVTAG_CVM  = combine(0x9F,0x34);
    public static final String EMVTAG_TERM_TYPE = combine(0x9F,0x35);
    public static final String EMVTAG_IFD  = combine(0x9F,0x1E);
    public static final String EMVTAG_DF      = combine(0x84);
    public static final String EMVTAG_APP_VER  = combine(0x9F,0x09);
    public static final String EMVTAG_TXN_SN      = combine(0x9F,0x41);
    public static final String EMVTAG_CARD_ID  = combine(0x9F,0x63);
    public static final String EMVTAG_AID  = combine(0x4F);
    public static final String EMVTAG_SCRIPT_RESULT  = combine(0xDF,0x31);
    public static final String EMVTAG_ARC  = combine(0x8A);
    public static final String EMVTAG_ISS_COUNTRY_CODE = combine(0x5F,0x28);
    public static final String EMVTAG_EC_AUTH_CODE  = combine(0x9F,0x74);
    public static final String EMVTAG_EC_BALANCE = combine(0x9F,0x79);
    public static final String EMVTAG_TSI = combine(0x9B);
    public static final String EMVTAG_APP_LABEL = combine(0x50);
    public static final String EMVTAG_APP_NAME = combine(0x9F,0x12);
    public static final String EMVTAG_CONTACT_NAME = combine(0x9F,0x4E); // 商户名称
    
    private static String combine(int...bytes)
    {
        String tag="";
        for(int i = 0;i < bytes.length;i++)
        {
            tag+=HexUtils.byteToHex((byte)bytes[i]);
        }
        return tag;
    }

    /**
     * 得到卡片数据
     */
    public static String[] getACardData()throws Exception{
        String[] tags={
                combine(0x5A),//卡号
                combine(0x9F,0x1F),//一磁道数据
                combine(0x57),//二磁道数据
                combine(0x5F,0x24),//有效期
                combine(0x5F,0x34)//卡片序列号
        };
        return tags;
    }

    /**
     * 得到55域数据
     * @return
     * @throws Exception
     */
    public static String [] get55Region()throws Exception{

        String[] tags={

        };
        return tags;
    }


    /**
     * 脚本结果通知上送报文中F55
     * @return
     * @throws Exception
     */
    public static String[] getLakalaScriptResultTag() throws Exception{
        String[] tags={
        combine(0x9F,0x33),
        combine(0x95),
        combine(0x9F,0x37),
        combine(0x9F,0x1E),
        combine(0x9F,0x10),
        combine(0x9F,0x26),
        combine(0x9F,0x36),
        combine(0x82),
        combine(0xDF,0x31),
        combine(0x9F,0x1A),
        combine(0x9A)
        };
        return tags;           
    }
    
    public static String[] getReadCardInfoTag() throws Exception{
        String[] tags={            
        combine(0x5A),
        combine(0x57),
        combine(0x5F,0x34)
        };
        return tags;       
    }
    
    /**
     * 拉卡拉接口55域用法一
     * 用于 联机消费
     * @return
     * @throws Exception
     */
    public static String[] getLakalaF55UseModeOneForOnlineSale() throws Exception{
        String[] tags={            
        combine(0x9F,0x26),
        combine(0x9F,0x27),
        combine(0x9F,0x10),
        combine(0x9F,0x37),
        combine(0x9F,0x36 ),
        combine(0x95),
        combine(0x9A),
        combine(0x9C),
        combine(0x9F,0x02),
        combine(0x5F,0x2A),
        combine(0x82),
        combine(0x9F,0x1A),
        combine(0x9F,0x03),
        combine(0x9F,0x33),
        combine(0x9F,0x34),
        combine(0x9F,0x35),
        combine(0x9F,0x1E),
        combine(0x84),
        combine(0x9F,0x09),
        combine(0x9F,0x41),
        //combine(0x8A), // 联机消费，读取出8Atag值是，拉卡拉后台返回格式错误，故注掉
        combine(0x9F,0x74),
        combine(0x91),
        combine(0x71),
        combine(0x72)
        };
        return tags;       
    }
    
    /**
     * 拉卡拉接口55域用法一
     * 用于 余额查询、消费、预授权
     * @return
     * @throws Exception
     */
    public static String[] getLakalaF55UseModeOne() throws Exception{
        String[] tags={                
        combine(0x9F,0x26),
        combine(0x9F,0x27),
        combine(0x9F,0x10),
        combine(0x9F,0x37),
        combine(0x9F,0x36 ),
        combine(0x95),
        combine(0x9A),
        combine(0x9C),
        combine(0x9F,0x02),
        combine(0x5F,0x2A),
        combine(0x82),
        combine(0x9F,0x1A),
        combine(0x9F,0x03),
        combine(0x9F,0x33),
        combine(0x9F,0x34),
        combine(0x9F,0x35),
        combine(0x9F,0x1E),
        combine(0x84),
        combine(0x9F,0x09),
        combine(0x9F,0x41),
        combine(0x8A),
        combine(0x9F,0x74),
        combine(0x91),
        combine(0x71),
        combine(0x72)
        };
        return tags;       
    }
    
    /**
     * 拉卡拉接口55域用法二
     * 消费、预授权冲正
     * @return
     * @throws Exception
     */
    public static String[] getLakalaF55UseModeTwo() throws Exception{
        String[] tags={                
        combine(0x9F,0x10),
        combine(0x9F,0x36 ),
        combine(0x95),
        combine(0x9F,0x1E),
        combine(0xDF,0x31)
    };
        return tags;       
    }
    
    /**
     * 拉卡拉接口55域用法三
     * 脚本通知（接口中未用到）
     * @return
     * @throws Exception
     */
    public static String[] getLakalaF55UseModeThree() throws Exception{
        String[] tags={                
        combine(0x9F,0x26),
        combine(0x9F,0x10),
        combine(0x9F,0x37),
        combine(0x9F,0x36 ),
        combine(0x95),
        combine(0x9A),
        combine(0x82),
        combine(0x9F,0x1A),
        combine(0x9F,0x33),
        combine(0x9F,0x1E),
        combine(0xDF,0x31)
    };
        return tags;       
    }
    
    /**
     * 现金充值、指定账户圈存、非指定账户圈存上送报文F55
     * @return
     * @throws Exception
     */
    public static String[] getLakalaTransferF55Tag() throws Exception{
        String[] tags={                
        combine(0x9F,0x26),
        combine(0x9F,0x27),
        combine(0x9F,0x10),
        combine(0x9F,0x37),
        combine(0x9F,0x36 ),
        combine(0x95),
        combine(0x9A),
        combine(0x9C),
        combine(0x9F,0x02),
        combine(0x5F,0x2A),
        combine(0x82),
        combine(0x9F,0x1A),
        combine(0x9F,0x03),
        combine(0x9F,0x33),
        combine(0x9F,0x34),
        combine(0x9F,0x35),
        combine(0x9F,0x1E),
        combine(0x84),
        combine(0x9F,0x09),
        combine(0x9F,0x41),
        combine(0x9F,0x63),
        combine(0x91),
        combine(0x71),
        combine(0x72)
    };
        return tags;       
    }
    
    /**
     * 现金充值撤销上送报文F55
     * @return
     * @throws Exception
     */
    public static String[] getLakalaCashValueVoidF55Tag() throws Exception{
        String[] tags={                
        combine(0x9F,0x1A),
        combine(0x9F,0x03),
        combine(0x9F,0x33),
        combine(0x9F,0x34),
        combine(0x9F,0x35),
        combine(0x9F,0x1E),
        combine(0x84),
        combine(0x9F,0x09),
        combine(0x9F,0x41),
        combine(0x9F,0x63),
        combine(0x91),
        combine(0x71),
        combine(0x72),
        combine(0x9F,0x26),
        combine(0x9F,0x27),
        combine(0x9F,0x10),
        combine(0x9F,0x37),
        combine(0x9F,0x36 ),
        combine(0x95),
        combine(0x9A),
        combine(0x9C),
        combine(0x9F,0x02),
        combine(0x5F,0x2A),
        combine(0x82)
    };
        return tags;       
    }
    
    /**
     * 圈存冲正上送报文F55
     * @return
     * @throws Exception
     */
    public static String[] getReversalF55Tag() throws Exception{
        String[] tags={    
        combine(0x95),
        combine(0x9F,0x1E),
        combine(0x9F,0x10),
        combine(0x9F,0x36),
        combine(0xDF,0x31)
    };
        return tags;       
    }
    
    /**
     * 纯电子现金卡（AID号为06结尾的）
     */
    public static String[] getAidNo() throws Exception{
        String[] tags={    
        combine(0x4F) // AID
    };
        return tags;
    }
    
    /**
     * 接触式读取内核数据，打印凭条使用
     * @return
     * @throws Exception
     */
    public static String[] getkernelDataForPrint() throws Exception{
        String[] tags={        
        combine(0x5F,0x34), // CSN
        combine(0x4F), // AID
        combine(0x9F, 0x26), // TC
        combine(0x95), // TVR
        combine(0x9B), // TSI
        combine(0x9F,0x36), // ATC
        combine(0x9F,0x37), // UNPR NUM
        combine(0x82), // AIP
        combine(0x9F,0x79), // 卡片余额
        combine(0x9F,0x33),  // TermCap
        combine(0x9F,0x10),  // IAD
        combine(0x50),   // APP LABEL
        combine(0x9F,0x12) // APP PREFERRED NAME
    };
        return tags;
    }
    
    /**
     * 快速支付读取内核数据，打印凭条使用
     * @return
     * @throws Exception
     */
    public static String[] getrfkernelDataForPrint() throws Exception{
        String[] tags={        
        combine(0x5F,0x34), // CSN
        combine(0x4F), // AID
        combine(0x9F, 0x26), // TC
        combine(0x95), // TVR
        combine(0x9B), // TSI
        combine(0x9F,0x36), // ATC
        combine(0x9F,0x37), // UNPR NUM
        combine(0x82), // AIP
        combine(0x9F,0x5D), // 卡片余额
        combine(0x9F,0x33),  // TermCap
        combine(0x9F,0x10),  // IAD
        combine(0x50),   // APP LABEL
        combine(0x9F,0x12) // APP PREFERRED NAME
    };
        return tags;
    }
    
    public static String[] getLakalaQueryBalanceTag() throws Exception{
        String[] tags={                
        combine(0x9F,0x26),
        combine(0x9F,0x27),
        combine(0x9F,0x10),
        combine(0x9F,0x37),
        combine(0x9F,0x36 ),
        combine(0x95),
        combine(0x9A),
        combine(0x9C),
        combine(0x9F,0x02),
        combine(0x5F,0x2A),
        combine(0x82),
        combine(0x9F,0x1A),
        combine(0x9F,0x03),
        combine(0x9F,0x33),
        combine(0x9F,0x34),
        combine(0x9F,0x35),
        combine(0x9F,0x1E),
        combine(0x84),
        combine(0x9F,0x09),
        combine(0x9F,0x41),
        combine(0x8A),
        combine(0x9F,0x74),
        combine(0x91),
        combine(0x71),
        combine(0x72)
    };
        return tags;       
    }
    
    public static String[] getBalanceTransferTag() throws Exception{
        String[] tags={                
        combine(0x9F,0x26),
        combine(0x9F,0x27),
        combine(0x9F,0x10),
        combine(0x9F,0x37),
        combine(0x9F,0x36 ),
        combine(0x95),
        combine(0x9A),
        combine(0x9C),
        combine(0x9F,0x02),
        combine(0x5F,0x2A),
        combine(0x82),
        combine(0x9F,0x1A),
        combine(0x9F,0x03),
        combine(0x9F,0x33),
        combine(0x9F,0x34),
        combine(0x9F,0x35),
        combine(0x9F,0x1E),
        combine(0x84),
        combine(0x9F,0x09),
        combine(0x9F,0x41),
        //combine(0x8A),圈存报文测试
        //combine(0x9F,0x74),
        combine(0x9F,0x63),
        combine(0x91),
        combine(0x71),
        combine(0x72)
    };
        return tags;       
    }
    
    public static String[] getNullTag() throws Exception{
        String[] tags={};           
        return tags;       
    }
}
