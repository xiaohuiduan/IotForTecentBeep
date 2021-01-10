import com.pi4j.io.gpio.*;
import com.tencent.iot.explorer.device.java.common.Status;
import com.tencent.iot.explorer.device.java.data_template.TXDataTemplateDownStreamCallBack;
import com.tencent.iot.explorer.device.java.mqtt.TXMqttActionCallBack;
import com.tencent.iot.explorer.device.java.server.samples.data_template.DataTemplateSample;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

/**
 * @author XiaoHui
 */
public class Main {
    /**
     * IOT平台URL
     */
    public static String mBrokerURL = "ssl://iotcloud-mqtt.gz.tencentdevices.com:8883";
    /**
     * 产品ID
     */
    public static String mProductID = "64ONICJ3N8";
    /**
     * 设备名称
     */
    public static String mDevName = "my_pi";
    /**
     * 设备密钥
     */
    public static String mDevPSK = "1ktYq8uojYiuJgX7iZxAoQ==";
    /**
     * 储存属性的json文件名
     */
    public static String mJsonFileName = "data.json";

    private static DataTemplateSample mDataTemplateSample;

    /**
     * 获得GPIO的控制器
     */
    public static final GpioController gpio = GpioFactory.getInstance();
    /**
     * GPIO输出，使用GPIO_00 ,默认输出为High
     */
    public static final GpioPinDigitalOutput beep = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "beep", PinState.HIGH);


    public static void main(String[] args) {
        // CallBack 代表的是MQTT协议的回调函数，MyDownCallback代表的是IOT平台下发消息的回调
        mDataTemplateSample = new DataTemplateSample(mBrokerURL, mProductID, mDevName, mDevPSK,
                null, null, new CallBack(), mJsonFileName, new MyDownCallback());
        // 进行连接
        mDataTemplateSample.connect();
        // 进行订阅，只有订阅后，消息才能下发。
        mDataTemplateSample.subscribeTopic();
        mDataTemplateSample.propertyClearControl();
    }

    /**
     * MQTT的回调函数，可以不用管
     */
    public static class CallBack extends TXMqttActionCallBack {

        @Override
        public void onConnectCompleted(Status status, boolean reconnect, Object userContext, String msg) {

        }

        @Override
        public void onConnectionLost(Throwable cause) {

        }

        @Override
        public void onDisconnectCompleted(Status status, Object userContext, String msg) {
        }

        @Override
        public void onPublishCompleted(Status status, IMqttToken token, Object userContext, String errMsg) {
        }

        @Override
        public void onSubscribeCompleted(Status status, IMqttToken asyncActionToken, Object userContext, String errMsg) {
        }

        @Override
        public void onMessageReceived(final String topic, final MqttMessage message) {

        }

    }

    /**
     * 实现下行消息处理的回调接口
     */
    private static class MyDownCallback extends TXDataTemplateDownStreamCallBack {

        @Override
        public void onReplyCallBack(String msg) {

        }

        @Override
        public void onGetStatusReplyCallBack(JSONObject data) {


        }

        /**
         * 在微信小程序点击关闭按钮后，IOT平台会向树莓派发送命令消息，此命令消息会在这这里进行回调
         * beep是低电平触发！！！！！！！！！！！！
         *
         * @param msg 接收到的消息。
         * @return
         */
        @Override
        public JSONObject onControlCallBack(JSONObject msg) {

            // 获得开关的数据，beep_switch是开关的标识符。尽管我们在云平台中定义的是布尔型数据，但是实际上腾讯云发送过来的是int类型数据。
            int power = msg.getInt("beep_switch");
            // 打开开关
            if (power == 1) {
                beep.low();
                System.out.println("打开");
            } else {
                // 关闭蜂鸣器
                beep.high();
                System.out.println("关闭");
            }
            // 返回消息
            JSONObject result = new JSONObject();
            result.put("code", 0);
            result.put("status", "ok");
            return result;
        }

        @Override
        public JSONObject onActionCallBack(String actionId, JSONObject params) {
            return null;
        }
    }

}
