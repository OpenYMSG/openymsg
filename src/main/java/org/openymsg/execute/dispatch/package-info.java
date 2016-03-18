/**
 * Dispatch handles the execution of jobs. There are two external interfaces that developers use: Dispatcher and
 * Request. Request is the job that should be executed and Dispatcher is the executor of that job. DispatcherImpl is the
 * implementation of Dispatcher. It handles building a ThreadPoolFactory and an Executor to execute the jobs. The thread
 * name will be the username, or whatever String is passed. The DispatcherImpl also wraps a Request into a
 * RequestWrapper to trap any Exceptions that should be reported back to the Request.
 * <img src="doc-files/dispatch.jpg" alt="Overview of Dispatch Framework"/>
 */
package org.openymsg.execute.dispatch;
