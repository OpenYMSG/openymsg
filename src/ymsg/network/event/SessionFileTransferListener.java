/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ymsg.network.event;

import java.util.List;

/**
 *
 * @author damencho
 */
public interface SessionFileTransferListener
{
    public void fileTransferRequestReceived(SessionFileTransferEvent ev);

    public void statusChanged(SessionFileTransferEvent ev);
}
