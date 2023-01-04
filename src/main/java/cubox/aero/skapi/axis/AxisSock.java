// *****************************************************************************
// (C) COPYRIGHT WINIX Information Inc. 2013
// All Rights Reserved
// Licensed Materials - Property of WINIX
//
// This program contains proprietary information of WINIX Information.
// All embodying confidential information, ideas and expressions can't be
// reproceduced, or transmitted in any form or by any means, electronic, 
// mechanical, or otherwise without the written permission of WINIX Information.
//
//  Components   : AxisSock.java
//  Rev. History :
//  		  Ver	Date	Description
//		  -------	-------	------------------------------------------------
//		    01.00	2013-05	Initial version
// *****************************************************************************
package cubox.aero.skapi.axis;

import axis.api.AxisStream;
import axis.api.AxisStream.axAux;
import axis.api.axMisc;
import axis.api.axSock;
import axis.api.axStreamH;
import axis.api.axStreamH.AuxH;
import axis.api.define.piAxisApi;
import org.springframework.stereotype.Service;

@Service
public class AxisSock {
    private axSock m_sock = null;        // works sock
    private String m_ipad;
    private int m_port;
    private AxisStream m_axisH;
    private byte[] m_errcod;
    private byte[] m_errmsg;

    public AxisSock() {
        m_sock = new axSock();
        m_axisH = new AxisStream();
    }

    public byte[] AxisCall(String TR, int flag, byte[] packet, int size) {
        byte[] senddata, recvdata, recvpacket;
        int recvl, axisL, offs;
        if (!m_sock.openSock(m_ipad, m_port))
            return null;
        m_axisH.clearAxisH();
        m_axisH.datL = size;
        m_axisH.msgK = AxisStream.msgK_AXIS;
        if ((flag & piAxisApi.fid_type) != 0)
            m_axisH.auxs = AxisStream.auxsOOP;
        m_axisH.winK = 1;
        byte[] trcd = TR.getBytes();
        axMisc.copyMemory(m_axisH.trxC, 0, trcd, 0, trcd.length);
        senddata = new byte[size + axStreamH.SizeStreamH];
        m_axisH.makeAxisH(senddata);
        axMisc.copyMemory(senddata, axStreamH.SizeStreamH, packet, 0, size);
        if (!m_sock.writeData(senddata, senddata.length))
            return null;
        recvdata = m_sock.recvData();
        if (recvdata == null)
            return null;
        offs = 0;
        recvl = recvdata.length;
        m_axisH.constructAxisH(recvdata, offs);
        offs += axStreamH.SizeStreamH;
        axisL = m_axisH.datL;
        if ((m_axisH.stat & AxisStream.statAUX) != 0) {
            axAux aux = new axAux(recvdata, offs);
            axisL -= AuxH.SizeAuxH;
            int auxl = 0xff & aux.datL;
            if (axisL <= 0 || axisL - auxl <= 0)
                return null;
            offs += AuxH.SizeAuxH;
            axisL -= AuxH.SizeAuxH;
            m_errcod = new byte[6];
            axMisc.copyMemory(m_errcod, 0, recvdata, offs, 6);
            m_errmsg = new byte[auxl - 6];
            axMisc.copyMemory(m_errmsg, 0, recvdata, offs + 6, (auxl - 6));
            offs += (auxl);
            axisL -= (auxl);
        }
        recvpacket = new byte[axisL];
        axMisc.copyMemory(recvpacket, 0, recvdata, offs, axisL);
        m_sock.closeSock();
        return recvpacket;
    }

    public void setInfo(String ip, int port) {
        m_ipad = ip;
        m_port = port;

    }

    public byte[] getErrCod() {
        return m_errcod;
    }

    public byte[] getErrMsg() {
        return m_errmsg;
    }

}

