package com.zx.zxtvsettings.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zx.zxboxlauncher.R;
import com.zx.zxboxlauncher.activity.BaseActivity;
import com.zx.zxtvsettings.Utils.Tools;
import com.zx.zxtvsettings.adapter.MyBluetoothAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * User: ShaudXiao
 * Date: 2016-08-22
 * Time: 15:15
 * Company: zx
 * Description:
 * FIXME
 */

public class BluethoothActivity extends BaseActivity {

    @BindView(R.id.bluetooth_iv_open)
    ImageView mBluetoothIvOpen;
    @BindView(R.id.bluetooth_rl_open)
    RelativeLayout mBluetoothRlOpen;
    @BindView(R.id.bluetooth_iv_detection)
    ImageView mBluetoothIvDetection;
    @BindView(R.id.bluetooth_rl_detection)
    RelativeLayout mBluetoothRlDetection;
    @BindView(R.id.bluetooth_iv_search)
    ImageView mBluetoothIvSearch;
    @BindView(R.id.bluetooth_rl_search)
    RelativeLayout mBluetoothRlSearch;
    @BindView(R.id.bluetooth_rl_setting)
    RelativeLayout mBluetoothRlSetting;
    @BindView(R.id.bluetooth_tv_pair)
    TextView mBluetoothTvPair;
    @BindView(R.id.bluetooth_tv_pair_name)
    TextView mBluetoothTvPairName;
    @BindView(R.id.bluetooth_rl_pair1)
    RelativeLayout mBluetoothRlPair1;
    @BindView(R.id.bluetooth_rl_pair)
    RelativeLayout mBluetoothRlPair;
    @BindView(R.id.bluetooth_tv_search_device)
    TextView mBluetoothTvSearchDevice;
    @BindView(R.id.bluetooth_lv_search_device)
    ListView mBluetoothLvSearchDevice;
    @BindView(R.id.bluetooth_rl_search_device)
    RelativeLayout mBluetoothRlSearchDevice;

    private Set<BluetoothDevice> bondedDevices;
    private BluetoothDevice pairDevice;

    private boolean openFlag;
    private boolean detectionFlag;
    private int pairPosition = -1;

    private Context context;

    private BluetoothAdapter bluetoothAdapter;
    private MyBluetoothAdapter itemAdapter;

    private List<Map<String, Object>> list;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_bluetooth;
    }

    @Override
    protected void setupViews() {
        mBluetoothLvSearchDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BluetoothDevice device = (BluetoothDevice) list.get(i).get("device");
                device.createBond();
            }
        });
    }

    @Override
    protected void initialized() {
        context = this;

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        list = new ArrayList<Map<String, Object>>();
        if(bluetoothAdapter.isEnabled()) {
            mBluetoothIvOpen.setBackgroundResource(R.drawable.switch_on);
            mBluetoothIvDetection.setBackgroundResource(R.drawable.switch_off);
            mBluetoothIvSearch.setVisibility(View.VISIBLE);
            mBluetoothTvPair.setVisibility(View.VISIBLE);
            mBluetoothRlPair.setVisibility(View.VISIBLE);
            mBluetoothLvSearchDevice.setVisibility(View.VISIBLE);
            mBluetoothRlSearchDevice.setVisibility(View.VISIBLE);

            bondedDevices = bluetoothAdapter.getBondedDevices();
            Iterator iterator = bondedDevices.iterator();
            if(iterator.hasNext()) {
                BluetoothDevice bond = (BluetoothDevice) iterator.next();
                pairDevice = bond;
                mBluetoothTvPairName.setText(bond.getName());
                pairPosition = -2;
            }
            openFlag = true;
        } else {
            mBluetoothIvOpen.setBackgroundResource(R.drawable.switch_off);
            mBluetoothIvDetection.setBackgroundResource(R.drawable.switch_off);
            mBluetoothIvSearch.setVisibility(View.GONE);
            mBluetoothTvPair.setVisibility(View.GONE);
            mBluetoothRlPair.setVisibility(View.GONE);
            mBluetoothTvSearchDevice.setVisibility(View.GONE);
            mBluetoothRlSearchDevice.setVisibility(View.GONE);
        }



    }

    @Override
    public void onResume() {
        super.onResume();
        resisgerReciver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(searchReceiver);
    }

    private void resisgerReciver() {
        IntentFilter intent = new IntentFilter();
        // 用BroadcastReceiver来取得搜索结果
        intent.addAction(BluetoothDevice.ACTION_FOUND);
        //每当扫描模式变化的时候，应用程序可以为通过ACTION_SCAN_MODE_CHANGED值来监听全局的消息通知。
        // 比如，当设备停止被搜寻以后，该消息可以被系统通知給应用程序。
        intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        //每当蓝牙模块被打开或者关闭，应用程序可以为通过ACTION_STATE_CHANGED值来监听全局的消息通知。
        intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(searchReceiver, intent);
    }

    @OnClick({R.id.bluetooth_rl_open, R.id.bluetooth_rl_detection, R.id.bluetooth_rl_pair1, R.id.bluetooth_rl_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bluetooth_rl_open:
                if(!openFlag) {
                    bluetoothAdapter.disable();
                    mBluetoothIvOpen.setBackgroundResource(R.drawable.switch_off);
                    mBluetoothIvDetection.setBackgroundResource(R.drawable.switch_off);
                    mBluetoothIvSearch.setVisibility(View.GONE);
                    mBluetoothTvPair.setVisibility(View.GONE);
                    mBluetoothRlPair.setVisibility(View.GONE);
                    mBluetoothTvSearchDevice.setVisibility(View.GONE);
                    mBluetoothRlSearchDevice.setVisibility(View.GONE);

                    openFlag = !openFlag;
                } else {
                    if(bluetoothAdapter != null) {
                        if(!bluetoothAdapter.isEnabled()) {
                            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivity(intent);
                        }
                    } else {
                        showToastShort(getString(R.string.bluetooth_disabled));
                    }

                    mBluetoothIvOpen.setBackgroundResource(R.drawable.switch_on);
                    mBluetoothIvSearch.setVisibility(View.VISIBLE);
                    mBluetoothTvPair.setVisibility(View.VISIBLE);
                    mBluetoothRlPair.setVisibility(View.VISIBLE);
                    mBluetoothLvSearchDevice.setVisibility(View.VISIBLE);
                    mBluetoothRlSearchDevice.setVisibility(View.VISIBLE);

                    openFlag = !openFlag;
                }
                break;
            case R.id.bluetooth_rl_detection:
                if(detectionFlag) {
                    mBluetoothIvDetection.setBackgroundResource(R.drawable.switch_off);
                    detectionFlag = !detectionFlag;
                } else {
                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivity(discoverableIntent);
                    mBluetoothIvDetection.setBackgroundResource(R.drawable.switch_on);
                    detectionFlag = !detectionFlag;
                }
                break;
            case R.id.bluetooth_rl_pair1:
                if(pairPosition > -1) {
                    BluetoothDevice device = (BluetoothDevice) list.get(pairPosition).get("device");
                    try {
                        boolean b = Tools.removeBond(device.getClass(), device);
                        if(b) {
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("name", device.getName());
                            map.put("type", device.getBluetoothClass().getDeviceClass());
                            map.put("device", device);
                            list.add(map);
                            itemAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();;
                    }
                } else if(pairPosition == -2) {
                    try {
                        showToastShort(getString(R.string.bluetooth_connect_canceling));
                        boolean b = Tools.removeBond(pairDevice.getClass(), pairDevice);
                        if (b) {
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("name", pairDevice.getName());
                            map.put("type", pairDevice.getBluetoothClass().getDeviceClass());
                            map.put("device", pairDevice);
                            mBluetoothTvPairName.setText(R.string.bluetooth_not_conected);
                            list.add(map);
                            itemAdapter.notifyDataSetChanged();
                        } else {
                            showToastShort(getString(R.string.bluetooth_connect_cancel_failed));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.bluetooth_rl_search:
                Animation rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate);
                mBluetoothIvSearch.startAnimation(rotateAnimation);
                bluetoothAdapter.startDiscovery();
                break;
        }
    }

    private BroadcastReceiver searchReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = null;
            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if(device!=null){
            }
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("name", device.getName());
                    map.put("type", device.getBluetoothClass().getDeviceClass());
                    map.put("device", device);
                    if (list.indexOf(map) == -1) {// 防止重复添加
                        list.add(map);
                        itemAdapter = new MyBluetoothAdapter(context, list);
                        mBluetoothLvSearchDevice.setAdapter(itemAdapter);
                    }
                }
            } else if (device != null && device.getBondState() == BluetoothDevice.BOND_BONDING) {
                showToastShort(getString(R.string.bluetooth_connectting));
            } else if (device != null && device.getBondState() == BluetoothDevice.BOND_BONDED) {
                mBluetoothTvPairName.setText(device.getName());
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).get("device").equals(device)) {
                        pairPosition = i;
                        list.remove(i);
                        itemAdapter.notifyDataSetChanged();
                    }
                }
                showToastShort(getString(R.string.bluetooth_connected));
            }
        }
    };
}
