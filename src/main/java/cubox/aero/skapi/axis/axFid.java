package cubox.aero.skapi.axis;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.swing.text.html.HTMLDocument.Iterator;

import axis.api.axGrid;
import axis.api.axMisc;
import org.springframework.stereotype.Component;

@Component
public class axFid
{
	private	static final char	sFS	= 0x0d;		// field separator	('\r')
	private static final char	sSS	= 0x13;		// Sub field separator
	private	static final char	sROW	= 0x0a;		// GRID row separator	('\n')
	private	static final char	sCELL	= 0x09;		// GRID cell separator	('\t')

	private	static final char	sDS	= 0x7f;		// input data separator (SYM + 0x7f + inDATA)
	private	static final char	sCOL	= 0x0a;		// GRID input column separator

	private static final int	FID_TYPE_OUT = 0;
	private static final int	FID_TYPE_IN  = 1;
	private static final int	FID_TYPE_GRID = 2;
	public static final int		GRID_FLAG_NEXT = 1;
	public static final int		GRID_FLAG_PREV = 2;
	public static final int		GRID_NEXT_ON = 1;
	public static final int		GRID_PREV_ON = 1;
	
	public ArrayList<trItem>	m_fid = null;
	
	public static class trItem
	{		
		String				m_trSymbol = null;	// tran symbol
		String				m_trData = null;
		int				m_fidType = 0;
		int				m_nextf = 0;		// next status of grid
		int				m_prevf = 0;		// prev status of grid
		int				m_nrow = 0;		// row counts of grid
		byte[]				m_save = null;
		byte[]				m_gridiheader = null;
		byte[]				m_gridoheader = null;
		public ArrayList<trItem>	m_subItem = null;	// GRID column info
		public ArrayList<trItem>	m_outItem = null;
	}

	public	axFid()
	{
		m_fid = new ArrayList<trItem>();
	}
	
	public void addFid(String fid, String data)
	{
		trItem	_fidItem;
		
		_fidItem = new trItem();
		_fidItem.m_trSymbol = new String(fid);
		if (data != null)
		{
			_fidItem.m_trData = new String(data);
			_fidItem.m_fidType = FID_TYPE_IN;
		}
		else
			_fidItem.m_fidType = FID_TYPE_OUT;
		m_fid.add(_fidItem);
	}
	
	public void addGridFid(String fid, trItem grid, int nrow, int flag)
	{
		if (fid == null || grid == null)
			return;
		grid.m_trSymbol = new String(fid);
		grid.m_fidType = FID_TYPE_GRID;
		if (grid.m_gridiheader == null)
		{
			grid.m_gridiheader = new byte[axGrid.sizeigrid];
			axMisc.fillMemory(grid.m_gridiheader, 0, axGrid.sizeigrid, (byte)' ');
		}
		
		if (nrow > 0)
		{
			String tmps = String.format("%04d",  nrow);
			for (int ii = 0; ii < axGrid.sizeinrow; ii++)
				grid.m_gridiheader[axGrid.inrow+ii] = (byte)tmps.charAt(ii);
		}
		switch(flag)
		{
		case GRID_FLAG_NEXT:
		case GRID_FLAG_PREV:
			if (grid.m_gridoheader == null)
				break;
			axMisc.copyMemory(grid.m_gridiheader, axGrid.isave, grid.m_save, 0, axGrid.sizeisave);
			if (flag == GRID_FLAG_NEXT)
				grid.m_gridiheader[axGrid.iikey] = (byte)'2';
			else
				grid.m_gridiheader[axGrid.iikey] = (byte)'1';
			break;
		}
		grid.m_gridiheader[axGrid.igdir] = '1';
		m_fid.add(grid);
	}
	
	public void deleteFid(String fid)
	{
		if (fid == null)
			return;
		
		java.util.Iterator<trItem> iterator = m_fid.iterator();
		
		while (iterator.hasNext()) {
		    trItem item = iterator.next();
		    if (fid.equals(item.m_trSymbol))
		    {
			    m_fid.remove(item);
			    break;
		    }
		     
		}
	}
	
	public void addGridCol(trItem grid, String fid, String data)
	{
		trItem	_fidItem;
		
		_fidItem = new trItem();
		_fidItem.m_trSymbol = new String(fid);
		if (data != null)
		{
			_fidItem.m_trData = new String(data);
			_fidItem.m_fidType = FID_TYPE_IN;
		}
		else
			_fidItem.m_fidType = FID_TYPE_OUT;
		if (grid.m_subItem == null)
			grid.m_subItem = new ArrayList<trItem>();
		grid.m_subItem.add(_fidItem);
	}
	
	public void delGridCol(trItem grid, String fid)
	{
		if (fid == null)
			return;
		
		java.util.Iterator<trItem> iterator = grid.m_subItem.iterator();
		
		while (iterator.hasNext()) {
		    trItem item = iterator.next();
		    if (fid.equals(item.m_trSymbol))
		    {
			    grid.m_subItem.remove(item);
			    break;
		    }    
		}
	}
	
	public String getFiddata(String fid)
	{
		int	ii;
		trItem	item;
		if (fid == null)
			return null;
		for (ii = 0; ii < m_fid.size(); ii++)
		{
			item = m_fid.get(ii);
			switch(item.m_fidType)
			{
			case FID_TYPE_IN:
			case FID_TYPE_GRID:
				continue;
			default:
				break;
			}
			if (item.m_trSymbol.equals(fid))
				return item.m_trData;
		}
		return null;
	}
	
	public String getGridData(String fid, int row, int col)
	{
		int	ii;
		trItem	item = null, colitem;
		if (fid == null || row <= 0 || col <= 0)
			return null;
		
		for (ii = 0; ii < m_fid.size(); ii++)
		{
			item = m_fid.get(ii);
			switch(item.m_fidType)
			{
			case FID_TYPE_IN:
			default:
				continue;
			case FID_TYPE_GRID:
				break;
			}
			if (!item.m_trSymbol.equals(fid))
				continue;
			break;
		}
		if (item == null)
			return null;
		if (item.m_outItem.size() < col)
			return null;
		colitem = item.m_outItem.get(col-1);
		if (colitem.m_outItem.size() < row)
			return null;
		return colitem.m_outItem.get(row-1).m_trData;
	}
	
	public byte[] makeData()
	{
		StringBuilder	senddata;
		trItem	item, gitem;
		int	ii, jj;
		
		senddata = new StringBuilder();
		for (ii = 0; ii < m_fid.size(); ii++)
		{
			item = m_fid.get(ii);
			switch(item.m_fidType)
			{
			case FID_TYPE_IN:
				senddata.append(item.m_trSymbol);
				senddata.append(sDS);
				senddata.append(item.m_trData);
				break;
			case FID_TYPE_GRID:
				senddata.append('$');
				senddata.append(item.m_trSymbol);
				senddata.append(sDS);
				senddata.append(axMisc.getString(item.m_gridiheader, 0, axGrid.sizeigrid));
				for (jj = 0; jj < item.m_subItem.size(); jj++)
				{
					gitem = item.m_subItem.get(jj);
					switch(gitem.m_fidType)
					{
					case FID_TYPE_IN:
						senddata.append(gitem.m_trSymbol);
						senddata.append(sDS);
						senddata.append(gitem.m_trData);
						break;
					case FID_TYPE_OUT:
					default:
						senddata.append(gitem.m_trSymbol);
						break;
					}
					senddata.append(sCOL);
				}
				break;
			case FID_TYPE_OUT:
			default:
				senddata.append(item.m_trSymbol);
				break;
			}
			senddata.append(sFS);
		}
		String sendString = senddata.toString();
		byte[] sendbuff = null;
		try
		{
			sendbuff = sendString.getBytes("MS949");
		} catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sendbuff;
	}
	
	public void parseFid(byte[] data)
	{
		int	ii, jj, kk, ll, mm, nn;
		trItem	item, col;
		
		for (ii = jj = kk = 0; ii < data.length; ii++)
		{
			switch(data[ii])
			{
			case sFS:
				break;
			default:
				continue;
			}
			
			for (item = null;kk < m_fid.size(); kk++)
			{
				item = m_fid.get(kk);
				switch(item.m_fidType)
				{
				case FID_TYPE_IN:
					continue;
				default:
					break;
				}
				break;
			}
			if (item == null)
				break;

			switch(item.m_fidType)
			{
			case FID_TYPE_OUT:
				item.m_trData = axMisc.getString(data, jj, ii - jj);
				break;
			case FID_TYPE_GRID:
				item.m_gridoheader = new byte[axGrid.sizeogrid];
				axMisc.copyMemory(item.m_gridoheader, 0, data, jj, axGrid.sizeogrid);
				item.m_save = new byte[axGrid.sizeosave];
				axMisc.copyMemory(item.m_save, 0, item.m_gridoheader, axGrid.osave, axGrid.sizeosave);
				byte npchk = item.m_gridoheader[axGrid.oxpos];
				if ((npchk & (byte)(0x01)) != 0)
					item.m_prevf = 1;
				else
					item.m_prevf = 0;
				if ((npchk & (byte)(0x02)) != 0)
					item.m_nextf = 1;
				else
					item.m_nextf = 0;
				jj += axGrid.sizeogrid;
				if (item.m_outItem == null)
					item.m_outItem = new ArrayList<trItem>();
				item.m_outItem.clear();
				item.m_nrow = 0;
				mm = nn = 0;
				for (ll = jj; ll < ii-1; ll++)
				{
					switch(data[ll])
					{
					case sCELL:
					case sROW:
						if (item.m_outItem.size() < (mm+1))
						{
							col = new trItem();
							item.m_outItem.add(col);
						}
						col = item.m_outItem.get(mm);
						if (col.m_outItem == null)
							col.m_outItem = new ArrayList<trItem>();
						trItem coldata = new trItem();
						coldata.m_trData = axMisc.getString(data, jj, ll - jj);
						col.m_outItem.add(coldata);
						mm++;
						jj = ll+1;
						if (data[ll] == sROW)
						{
							mm = 0;
							item.m_nrow++;
						}
						break;
					}
				}
				break;
			}
			kk++;
			jj = ii+1;
		}
	}
}

