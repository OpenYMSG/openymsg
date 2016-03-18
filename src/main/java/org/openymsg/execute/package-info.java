/**
 * Execution Framework The Execution Framework is made of 3 parts: Dispatch, Read and Write. Dispatch is the heart of
 * the framework and handles the execution of jobs. Each job will be alerted if it has an exception. Jobs can also be
 * scheduled to reoccur at regular intervals Read and Write are the parts that handle the sending and receiving of
 * messages using the Yahoo protocol. <img src="doc-files/execute.jpg" alt="Overview of Execution Framework"/>
 */
package org.openymsg.execute;
