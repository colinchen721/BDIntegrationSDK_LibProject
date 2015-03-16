package com.chipsguide.app.colorbluetoothlamp.v2.utils;import java.util.Random;import android.bluetooth.BluetoothDevice;import android.content.Context;import com.chipsguide.app.colorbluetoothlamp.v2.application.CustomApplication;import com.chipsguide.lib.bluetooth.extend.devices.BluetoothDeviceColorLampManager;import com.chipsguide.lib.bluetooth.extend.devices.BluetoothDeviceColorLampManager.OnBluetoothDeviceColorLampStatusChangedListener;import com.chipsguide.lib.bluetooth.extend.devices.BluetoothDeviceCommonLampManager;import com.chipsguide.lib.bluetooth.extend.devices.BluetoothDeviceCommonLampManager.OnBluetoothDeviceCommonLampStatusChangedListener;import com.chipsguide.lib.bluetooth.interfaces.callbacks.OnBluetoothDeviceManagerReadyListener;import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceManager;public class LampManager implements OnBluetoothDeviceManagerReadyListener,		OnBluetoothDeviceCommonLampStatusChangedListener,		OnBluetoothDeviceColorLampStatusChangedListener {	MyLogger flog = MyLogger.fLog();	private static LampManager mLampManager;	private Context mContext;	private BluetoothDeviceManager mBluetoothDeviceManager;	private BluetoothDeviceCommonLampManager mBluetoothDeviceCommonLampManager;	private BluetoothDeviceColorLampManager mBluetoothDeviceColorLampManager;	private boolean colorLamp = false;// 彩灯	private boolean isLampState = false;// 灯的开关状态	private LampManager(Context context)	{		mContext = context;	}	public static LampManager getInstance(Context context)	{		if (mLampManager == null)		{			mLampManager = new LampManager(context);		}		return mLampManager;	}	public void init()	{		mBluetoothDeviceManager = ((CustomApplication) mContext				.getApplicationContext()).getBluetoothDeviceManager();		mBluetoothDeviceManager.setOnBluetoothDeviceManagerReadyListener(this);	}	@Override	public void onBluetoothDeviceManagerReady()	{		flog.d("onBluetoothDeviceManagerReady");		mBluetoothDeviceCommonLampManager = mBluetoothDeviceManager				.getBluetoothDeviceCommonLampManager();		mBluetoothDeviceColorLampManager = mBluetoothDeviceManager				.getBluetoothDeviceColorLampManager();		mBluetoothDeviceColorLampManager				.setOnBluetoothDeviceColorLampStatusChangedListener(this);		mBluetoothDeviceCommonLampManager				.setOnBluetoothDeviceCommonLampStatusChangedListener(this);		mBluetoothDeviceColorLampManager				.getStatus(BluetoothDeviceColorLampManager.StatusType.STATUS);		mBluetoothDeviceCommonLampManager.getLampstatus();	}	/**	 * commandType是反馈还是查询。 	 * on彩灯开关。 	 * colorlamp【on-off】。 	 * brightness彩灯亮度（彩灯亮度不变化）。	 * red-green-blue【RGB值】。 	 * rhythm灯效。	 */	@Override	public void onBluetoothDeviceColorLampStatusChanged(int commandType,			boolean on, int brightness, int red, int green, int blue, int rhythm)	{		flog.d("彩灯 commandType " + commandType + " on : " + on);		isLampState = on;		colorLamp = true;		commandTypeBack(commandType);	}	/**	 * commandType反馈or查询 	 * on白灯开关 	 * brightness白灯开关	 */	@Override	public void onBluetoothDeviceCommonLampStatusChanged(int commandType,			boolean on, int brightness)	{		flog.d("白灯 commandType " + commandType + " on : " + on);		isLampState = on;		colorLamp = false;		commandTypeBack(commandType);	}		private void commandTypeBack(int commandType)	{		switch (commandType)		{		case BluetoothDeviceColorLampManager.CommandType.INQUIRY:			if(isLampState)			{				lamp.onLampStateInqiryBackChange(colorLamp, isLampState);			}			break;		case BluetoothDeviceColorLampManager.CommandType.FEEDBACK:				lamp.onLampStateFeedBackChange(colorLamp, isLampState);			break;		}	}	/**	 * @param red	 * @param green	 * @param blue	 */	public void setColor(int red, int green, int blue)	{		if (mBluetoothDeviceColorLampManager != null)		{			mBluetoothDeviceColorLampManager.setColor(red, green, blue);		}	}		public BluetoothDevice getBluetoothDevice()	{		if (mBluetoothDeviceManager != null)		{			return mBluetoothDeviceManager.getBluetoothDeviceConnectedA2dp();		}		return null;	}		/**	 * @param brightness	 */	public void setBrightness(int brightness)	{		if (mBluetoothDeviceColorLampManager != null)		{			mBluetoothDeviceCommonLampManager.setBrightness(brightness);		}	}	/**	 * @param effect灯效	 */	public void setLampEffect(int effect)	{		setLampEffect(effect, 0);	}	/**	 * @param effect灯效	 * @param velocity灯效速度[2-49]	 */	public void setLampEffect(int effect, int velocity)	{		if (mBluetoothDeviceColorLampManager != null)		{			if (velocity == 0)				mBluetoothDeviceColorLampManager.setEffect(effect);			else				mBluetoothDeviceColorLampManager						.setLampEffect(effect, velocity);		}	}	public void turnCommonOn()	{		if (mBluetoothDeviceCommonLampManager != null)		{			mBluetoothDeviceCommonLampManager.turnOn();		}	}	public void turnCommonOff()	{		if (mBluetoothDeviceCommonLampManager != null)		{			mBluetoothDeviceCommonLampManager.turnOff();		}	}	public void turnColorOn()	{		if (mBluetoothDeviceColorLampManager != null)		{			mBluetoothDeviceColorLampManager.turnOn();		}	}	public void turnColorOff()	{		if (mBluetoothDeviceColorLampManager != null)		{			mBluetoothDeviceColorLampManager.turnOff();		}	}	public void randomColor()	{		Random r = new Random();		int red = r.nextInt(255) + 1;// 范围是[0+1,255)		int green = r.nextInt(255) + 1;// 范围是[0+1,255)		int blue = r.nextInt(255) + 1;// 范围是[0+1,255)		setColor(red, green, blue);	}	public void lampOn()	{		if (colorLamp)		{			turnColorOn();		} else		{			turnCommonOn();		}	}	public void lampOff()	{		if (colorLamp)		{			turnColorOff();		} else		{			turnCommonOff();		}	}	/**	 * 开关灯	 */	public void LampOnorOff()	{		if (isLampState)		{			lampOff();		} else		{			lampOn();		}	}	public void getStatus()	{		if (mBluetoothDeviceColorLampManager != null)		{			mBluetoothDeviceColorLampManager					.getStatus(BluetoothDeviceColorLampManager.StatusType.STATUS);			mBluetoothDeviceCommonLampManager.getLampstatus();		}	}	@Override	public void onBluetoothDeviceColorLampStatusChanged(int arg0, boolean arg1,			int arg2, int arg3, int arg4, int arg5, int arg6, int arg7,			int arg8, int arg9, int arg10)	{		// TODO Auto-generated method stub		flog.d("--------------");	}		private LampListener lamp;		public void setLampListener(LampListener lamp)	{		this.lamp = lamp;	}		public interface LampListener {				void onLampStateInqiryBackChange(boolean colorState,boolean OnorOff);				void onLampStateFeedBackChange(boolean colorState,boolean OnorOff);			}}