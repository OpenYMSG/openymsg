package ymsg.network;

public interface ServiceConstants
{   public final static int SERVICE_LOGON           = 0x01;
    public final static int SERVICE_LOGOFF          = 0x02;

    public final static int SERVICE_ISAWAY          = 0x03;
    public final static int SERVICE_ISBACK          = 0x04;
    public final static int SERVICE_IDLE            = 0x05;

    public final static int SERVICE_MESSAGE         = 0x06;
    public final static int SERVICE_MESSAGE_ACK     = 0xfb;


    public final static int SERVICE_IDACT           = 0x07;
    public final static int SERVICE_IDDEACT         = 0x08;

    public final static int SERVICE_MAILSTAT        = 0x09;
    public final static int SERVICE_USERSTAT        = 0x0a;
    public final static int SERVICE_NEWMAIL         = 0x0b;
    public final static int SERVICE_CHATINVITE      = 0x0c;
    public final static int SERVICE_CALENDAR        = 0x0d;
    public final static int SERVICE_NEWPERSONMAIL   = 0x0e;
    public final static int SERVICE_CONTACTNEW      = 0x0f;

    public final static int SERVICE_ADDIDENT        = 0x10;
    public final static int SERVICE_ADDIGNORE       = 0x11;
    public final static int SERVICE_PING            = 0x12;
    public final static int SERVICE_GROUPRENAME     = 0x13;
    public final static int SERVICE_SYSMESSAGE      = 0x14;
    public final static int SERVICE_PASSTHROUGH2    = 0x16;

    public final static int SERVICE_CONFINVITE      = 0x18;
    public final static int SERVICE_CONFLOGON       = 0x19;
    public final static int SERVICE_CONFDECLINE     = 0x1a;
    public final static int SERVICE_CONFLOGOFF      = 0x1b;
    public final static int SERVICE_CONFADDINVITE   = 0x1c;
    public final static int SERVICE_CONFMSG         = 0x1d;

    /*public final static int SERVICE_CHATLOGON     = 0x1e;
    public final static int SERVICE_CHATLOGOFF      = 0x1f;*/
    public final static int SERVICE_CHATPM          = 0x20;

    public final static int SERVICE_GAMELOGON       = 0x28;
    public final static int SERVICE_GAMELOGOFF      = 0x29;
    public final static int SERVICE_GAMEMSG         = 0x2a;

    public final static int SERVICE_FILETRANSFER    = 0x46;
    public final static int SERVICE_VOICECHAT       = 0x4a;
    public final static int SERVICE_NOTIFY          = 0x4b;
    public final static int SERVICE_P2PFILEXFER     = 0x4d;
    public final static int SERVICE_PEERTOPEER      = 0x4f;
    public final static int SERVICE_AUTHRESP        = 0x54;
    public final static int SERVICE_LIST            = 0x55;
    public final static int SERVICE_AUTH            = 0x57;

    public final static int SERVICE_FRIENDADD       = 0x83;
    public final static int SERVICE_FRIENDREMOVE    = 0x84;
    public final static int SERVICE_CONTACTIGNORE   = 0x85;
    public final static int SERVICE_CONTACTREJECT   = 0x86;

    public final static int SERVICE_CHATCONNECT     = 0x96;
    public final static int SERVICE_CHATGOTO        = 0x97; // ?
    public final static int SERVICE_CHATLOGON       = 0x98;
    public final static int SERVICE_CHATLEAVE       = 0x99; // ?
    public final static int SERVICE_CHATLOGOFF      = 0x9b;
    public final static int SERVICE_CHATDISCONNECT  = 0xa0;
    public final static int SERVICE_CHATPING        = 0xa1; // ?
    public final static int SERVICE_CHATMSG         = 0xa8;

    public final static int SERVICE_Y7_AUTHORIZATION = 0xd6;    /* YMSG13 */

    public final static int SERVICE_Y6_VISIBILITY_TOGGLE= 0xc5;  // 197
    public final static int SERVICE_Y6_STATUS_UPDATE= 0xc6;
    public final static int SERVICE_STATUS_15 = 0xf0;
    public final static int SERVICE_LIST_15 = 0xf1;

    public final static int SERVICE_FILETRANS_15 = 0xdc;        //220
    public final static int SERVICE_FILETRANS_INFO_15 = 0xdd;   //221
    public final static int SERVICE_FILETRANS_ACC_15 = 0xde;    //222

    // Yahoo buddy icons
    public final static int SERVICE_PICTURE_CHECKSUM= 0xbd;
    public final static int SERVICE_PICTURE         = 0xbe;
    public final static int SERVICE_PICTURE_UPDATE  = 0xc1;
    public final static int SERVICE_PICTURE_UPLOAD  = 0xc2;
    public final static int SERVICE_AVATAR  = 0xbc;
    public final static int SERVICE_AVATAR_UPDATE  = 0xc7;

    // -----Home made service numbers, used in event dispatch only
    final static int SERVICE_X_ERROR                = 0xf00;
    final static int SERVICE_X_OFFLINE              = 0xf01;
    final static int SERVICE_X_EXCEPTION            = 0xf02;
    final static int SERVICE_X_BUZZ                 = 0xf03;
    final static int SERVICE_X_CHATUPDATE           = 0xf04;


    // service      jYMSG9                  libyahoo2
    // -----------------------------------------------------
    //  0x0f        CONTACTNEW              NEWCONTACT
    //  0x83        FRIENDADD               ADDBUDDY
    //  0x84        FRIENDREMOVE            REMBUDDY
    //  0x85        CONTACTIGNORE           IGNORECONTACT
    //  0x86        CONTACTREJECT           REJECTCONTACT
    //  0x96        CHATCONNECT             CHATONLINE
    //  0x98        CHATLOGON               CHATJOIN
    //  0x9b        CHATLOGOFF              CHATEXIT
    //  0xa0        CHATDISCONNECT          CHATLOGOFF
    //  0xa8        CHATMSG                 COMMENT
}
