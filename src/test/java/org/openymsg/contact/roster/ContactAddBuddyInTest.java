package org.openymsg.contact.roster;

public class ContactAddBuddyInTest {

	String MoveContactToGroupAckIn = "Magic:YMSG Version:16 Length:109 Service:Y7_CHANGE_GROUP Status:SERVER_ACK SessionId:0x59e41a  [1] [neilvhart] [302] [240] [300] [240] [7] [neilvhart] [66] [0] [224] [BuddiesYJ0] [264] [BuddiesYJ1] [301] [240] [303] [240]";
	String MoveContactToGroupOut = "Magic:YMSG Version:16 Length:102 Service:Y7_CHANGE_GROUP Status:DEFAULT SessionId:0x59e41a  [1] [neilvhart] [302] [240] [300] [240] [7] [neilvhart] [224] [BuddiesYJ0] [264] [BuddiesYJ1] [301] [240] [303] [240]";
	String RenameGroupAckIn = "Magic:YMSG Version:16 Length:46 Service:GROUPRENAME Status:SERVER_ACK SessionId:0x59e41a  [1] [neilvhart] [65] [BBB] [66] [0] [67] [BuddiesYJ0]";
	String RenameGroupOut = "Magic:YMSG Version:16 Length:39 Service:GROUPRENAME Status:DEFAULT SessionId:0x59e41a  [1] [neilvhart] [65] [BuddiesYJ0] [67] [BBB]";
	String Status15SingleAddedBuddyMacIn = "Magic:YMSG Version:16 Length:153 Service:STATUS_15 Status:SERVER_ACK SessionId:0x59e41a  [302] [315] [300] [315] [7] [neilnexthart] [10] [0] [13] [1] [192] [-1031506521] [198] [0] [213] [2] [244] [11378463] [550] [TR6H2CBCG4IVWYNTAYMFROEWIA] [301] [315] [303] [315]";

}
