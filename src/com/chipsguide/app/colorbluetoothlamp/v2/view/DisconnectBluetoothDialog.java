//package com.chipsguide.app.colorbluetoothlamp.v2.view;
//
//import android.app.Dialog;
//import android.bluetooth.BluetoothDevice;
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//
//import com.chipsguide.app.colorbluetoothlamp.v2.R;
//import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceManager;
//
//public class DisconnectBluetoothDialog extends Dialog implements OnClickListener{
//	private BluetoothDevice mConnectInfoConnected;
//	private Context mContext;
//	private Handler mHandler;
//	private Button mButtonCancel;
//	private Button mButtonCommit;
//	private TextView mMessage;
//	private BluetoothDeviceManager mBluetoothManager;
//	
//	public DisconnectBluetoothDialog(Context context, int theme,Handler mHandler, BluetoothDevice device)
//	{
//		super(context, theme);
//		this.mContext = context;
//		this.mHandler = mHandler;
//		
//		this.mConnectInfoConnected = device;
//	}
//	
//	public DisconnectBluetoothDialog(Context context, int theme,Handler mHandler,
//			BluetoothDevice device,
//			BluetoothDeviceManager mBluetoothManager)
//	{
//		super(context, theme);
//		this.mContext = context;
//		this.mHandler = mHandler;
//		this.mConnectInfoConnected = device;
//		this.mBluetoothManager = mBluetoothManager;
//	}
//	
//	@SuppressWarnings("deprecation")
//	@Override
//	protected void onCreate(Bundle savedInstanceState)
//	{
//		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
//				5 * this.getWindow().getWindowManager().getDefaultDisplay()
//						.getWidth() / 6, ViewGroup.LayoutParams.WRAP_CONTENT);
//		this.setCanceledOnTouchOutside(false);
//		this.setContentView(LayoutInflater.from(this.mContext).inflate(
//						R.layout.dialog_disconnect_bluetooth, null, false), params);
//		this.mMessage = (TextView) this.findViewById(R.id.textv_message);
//		this.mButtonCommit = (Button) this.findViewById(R.id.button_ok);
//		this.mButtonCancel = (Button) this.findViewById(R.id.button_cancel);
//		mMessage.setText(mContext.getResources().getString(R.string.bluetooth_discon_hint));
//		this.mButtonCommit.setOnClickListener(this);
//		this.mButtonCancel.setOnClickListener(this);
//	}
//	
//	@Override
//	public void onClick(View view)
//	{
//		switch (view.getId())
//		{
//			case R.id.button_ok:
//				Message msg = new Message();
//				msg.what = 01;
//				mHandler.sendMessage(msg);
//				mBluetoothManager.disconnect(mConnectInfoConnected);
//				this.dismiss();
//				break;
//			case R.id.button_cancel:
//				this.dismiss();
//				break;
//			default:
//			break;
//		}
//	}
//
//}
