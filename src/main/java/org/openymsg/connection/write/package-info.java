/**
 * Framework for writing messages to Yahoo.  The interfaces that a developer uses are Message and PacketWriter. 
 * Each subclass of Message will 
 * supply the necessary information for the message packet ot Yahoo.  
 * PacketWriter handles taking that Message and getting it on the Yahoo 
 * connection.
 * The implementation of PacketWriter is PacketWriterImpl.  It uses the Dispatcher to executor a job containing
 * the Message.  A MessageExecuteRequest is what wraps the Messages.
 *  <img src="doc-files/write.jpg" alt="Overview of Write Framework"/> 
 */
package org.openymsg.connection.write;

