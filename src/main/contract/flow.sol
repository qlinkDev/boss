pragma solidity ^0.4.13;
/// @title 设备流量合约
contract DeviceFlowContract {

	// 自定义类型：设备流量
	struct DeviceFlow {
		bytes32 imei;   		// 设备编号（32字节）
		uint flow;   			// 流量
	}
	
    // 一个存储`DeviceFlow`结构的动态数组
    DeviceFlow[] private deviceFlows;
	
    // 存储设备流量数据
    function set(bytes32 imei, uint flow) {
       
        bool stored = false;  // 如果为真，则表示该设备已经存储只需要修改。
        // 循环deviceFlows数据，如果设备已存在则修改对象，如果不存在则创建一个DeviceFlow对象并添加到deviceFlows
        for (uint i=0; i<deviceFlows.length; i++) {
        	if (deviceFlows[i].imei == imei) {
        		deviceFlows[i].flow = deviceFlows[i].flow + flow;
        		stored = true;
        		break;
       		}
   	 	}
   	 	if (!stored) {
   	 		deviceFlows.push(DeviceFlow({
                imei: imei,
                flow: flow
            }));
   		}
    }
    
    // 获取设备流量设备
    function get(bytes32 imei) returns (uint) {
    	// 循环deviceFlows数据，找到设备流量对象，返回流量
        for (uint i=0; i<deviceFlows.length; i++) {
        	if (deviceFlows[i].imei == imei) {
        		return deviceFlows[i].flow;
       		}
   	 	}
    }	
}