pragma solidity ^0.4.13;

/*********************** Qlc消耗相关智能合约 ***********************/
/// @title Qlc数据字典
contract QlcDataDictionary {
	
	mapping (address => uint256) public balanceOf;	// 地址和地址所具有的QLC数量数组

	// 增加
	function increase(address _target, uint256 _value) returns (bool success) {
		balanceOf[_target] += _value;
		return true;
	}
	
	// 减少
	function reduce(address _target, uint256 _value) returns (bool success) {
		require (_value < balanceOf[_target]);     // 检查余额是否足够
		balanceOf[_target] -= _value;
		return true;
	}
}

/// @title QLC交易记录
contract QlcRecord {

	// 自定义类型：QLC交易记录
	struct QlcRecord {
    	address record_address;		// 记录所属地址
		uint record_type;   		// 记录类型(0_增加,1_减少)
		uint amount;   				// QLC数量
		uint time;   				// 发生时间(毫秒)
		string description;		// 描述
	}
	
    // 一个存储`QlcRecord`结构的动态数组
    QlcRecord[] private qlcRecords;
	
    // 存储设备流量数据
    function qlcRecord_add(address record_address, uint record_type, uint amount, uint time, string description) {   		
   		// 存储
 		qlcRecords.push(QlcRecord({
            record_address: record_address,
            record_type: record_type,
            amount: amount,
            time: time,
            description: description
        }));
    }
}

/// @title 设备流量记录
contract DeviceFlowRecordContract is QlcRecord, QlcDataDictionary {

	// 自定义类型：设备流量
	struct DeviceFlowRecord {
		bytes32 imei;   			// 设备编号（32字节）
    	address user_address;		// 设备所属用户对应的地址
		uint used_flow;   			// 已使用流量(M)
		uint time;   				// 发生时间(毫秒)
		uint flow_ratio;   			// QLC与流量比率(1M对应的QLC*1000)
		uint time_ratio;   			// QLC与时间比率(1分钟对应的QLC*1000)
		uint flow_sign;				// 流量间隔(M)
		uint time_sign;				// 时间间隔(毫秒)
	}
	
    // 一个存储`DeviceFlowRecord`结构的动态数组
    DeviceFlowRecord[] private deviceFlowRecords;
	
    // 存储设备流量数据
    function deviceFlowRecord_add(bytes32 imei, address user_address, uint used_flow, uint time, uint flow_ratio, uint time_ratio, uint flow_sign, uint time_sign) {
       
        DeviceFlowRecord deviceFlowRecord = null;  // 如果为真，则表示该设备已经存储只需要修改。
        // 循环deviceFlowRecords数据，如果设备已存在则修改对象，如果不存在则创建一个DeviceFlowRecord对象并添加到deviceFlowRecords
        for (uint i=0; i<deviceFlowRecords.length; i++) {
        	if (deviceFlowRecords[i].imei == imei) {
        		deviceFlowRecord = deviceFlowRecords[i];
        		break;
       		}
   	 	}
   	 	// 如果两次记录时间之差大于时间间隔则计算QLC使用量
   	 	if (deviceFlowRecord != null) {
   	 		uint flow_qlc = 0;	// 流量消耗QLC
   	 		uint time_qlc = 0;	// 时间消耗QLC
   	 		if ((used_flow - deviceFlowRecord.used_flow) > flow_sign) {
   	 			uint flow_qlc = (used_flow - deviceFlowRecord.used_flow) * flow_ratio;
   	 		}
   	 		if ((time - deviceFlowRecord.time) > time_sign) {
   	 			uint time_qlc = (time - deviceFlowRecord.time) * time_ratio;
   	 		}
   	 		uint qlc = (flow_qlc + time_qlc);
   	 		if (qlc > 0) {
   	 			// 调用数字资产消耗QLC方法
   	 			reduce(user_address, qlc);
   	 			// 记录数据资产交易记录
   	 			qlcRecord_add(user_address, 1, qlc, time, "flow QLC:" + flow_qlc + ",time QLC" + time_qlc);
   	 		}
   		}
   		
   		// 存储
 		deviceFlowRecords.push(DeviceFlowRecord({
            imei: imei,
            user_address: user_address,
            used_flow: used_flow,
            time: time,
            flow_ratio: flow_ratio,
            time_ratio: time_ratio,
            flow_sign: flow_sign,
            time_sign: time_sign
        }));
    }
}

/*********************** 代币相关智能合约 ***********************/
/* 定义合约拥有者 */
contract owned {
	
    /* 合约拥有者地址 */
    address public owner;

    function owned() {
        owner = msg.sender;
    }

    modifier onlyOwner {
        require(msg.sender == owner);
        _;
    }
	
	/* 合约所有权转让 */
    function transferOwnership(address newOwner) onlyOwner {
        owner = newOwner;
    }
}

contract tokenRecipient { 
	function receiveApproval(address from, uint256 value, address token, bytes extraData); 
}

/* 基本合约 */
contract token { 
	
	string public name; 				// 合约名称
	string public symbol; 				// 合约三个字母的别名
	uint8 public decimals; 				// 小数点保留位数
	uint256 public totalSupply;			// 总发行代币数量
	
	mapping (address => uint256) public balanceOf;	// 地址和地址所具有的代币数量数组
  	mapping (address => mapping (address => uint256)) public allowance;

	/* This generates a public event on the blockchain that will notify clients */
	event Transfer(address indexed from, address indexed to, uint256 value);

	/* This notifies clients about the amount burnt */
	event Burn(address indexed from, uint256 value);

	/* Initializes contract with initial supply tokens to the creator of the contract */
	function token(uint256 initialSupply, string tokenName, uint8 decimalUnits, string tokenSymbol) {
		balanceOf[msg.sender] = initialSupply;              // Give the creator all initial tokens
		totalSupply = initialSupply;                        // Update total supply
		name = tokenName;                                   // Set the name for display purposes
		symbol = tokenSymbol;                               // Set the symbol for display purposes
		decimals = decimalUnits;                            // Amount of decimals for display purposes
	}

	/* 内部转让，只能由本合约调用 */
	function _transfer(address _from, address _to, uint _value) internal {
		require (_to != 0x0);                               // Prevent transfer to 0x0 address. Use burn() instead
		require (balanceOf[_from] > _value);                // Check if the sender has enough
		require (balanceOf[_to] + _value > balanceOf[_to]); // Check for overflows
		balanceOf[_from] -= _value;                         // Subtract from the sender
		balanceOf[_to] += _value;                           // Add the same to the recipient
		Transfer(_from, _to, _value);
	}

	/// @notice 从调用者的地址向'_to'地址发送代币
	/// @param _to 代币接收者地址
	/// @param _value 发送的数量
	function transfer(address _to, uint256 _value) {
		_transfer(msg.sender, _to, _value);
	}

	/// @notice Send `_value` tokens to `_to` in behalf of `_from`
	/// @param _from The address of the sender
	/// @param _to The address of the recipient
	/// @param _value the amount to send
	function transferFrom(address _from, address _to, uint256 _value) returns (bool success) {
		require (_value < allowance[_from][msg.sender]);     // Check allowance
		allowance[_from][msg.sender] -= _value;
		_transfer(_from, _to, _value);
		return true;
	}
	
	/// @notice Send `_value` tokens to `_to` in behalf of `_from`
	/// @param _from The address of the sender
	/// @param _to The address of the recipient
	/// @param _value the amount to send
	function rechargeDataDictionary(address _from, address _to, uint256 _value) returns (bool success) {
		// 从用户账户减少QLC到总账户
		transferFrom(_from, _to, _value);
		// 用户数字资产增加
		increase(_from, _value);
		// 记录数据资产交易记录
   	 	qlcRecord_add(_from, 0, _value, now, "recharg:" + _value);
		return true;
	}

	/// @notice Allows `_spender` to spend no more than `_value` tokens in your behalf
	/// @param _spender The address authorized to spend
	/// @param _value the max amount they can spend
	function approve(address _spender, uint256 _value) returns (bool success) {
		allowance[msg.sender][_spender] = _value;
		return true;
	}

	/// @notice Allows `_spender` to spend no more than `_value` tokens in your behalf, and then ping the contract about it
	/// @param _spender The address authorized to spend
	/// @param _value the max amount they can spend
	/// @param _extraData some extra information to send to the approved contract
	function approveAndCall(address _spender, uint256 _value, bytes _extraData)
		returns (bool success) {
		tokenRecipient spender = tokenRecipient(_spender);
		if (approve(_spender, _value)) {
		  spender.receiveApproval(msg.sender, _value, this, _extraData);
		  return true;
		}
	}        

	/// @notice 从合约所有者地址上减少代币发行的总量
	/// @param _value 减少代币的数量
	function burn(uint256 _value) returns (bool success) {
		require (balanceOf[msg.sender] > _value);             // 判断合约所有者的代币数量是否足够
		balanceOf[msg.sender] -= _value;                      // 减少合约所有者的代币数量
		totalSupply -= _value;                                // 修改合约代币总数
		Burn(msg.sender, _value);
		return true;
	}
	
	/// @notice 从指定地址上减少代币发行的总量
	/// @param _from 指定的地址
	/// @param _value 减少代币的数量
	function burnFrom(address _from, uint256 _value) returns (bool success) {
		require(balanceOf[_from] >= _value);                // 检查目标地址余额是否足够
		require(_value <= allowance[_from][msg.sender]);    // Check allowance
		balanceOf[_from] -= _value;                         // 减少目标地址的代币数量
		allowance[_from][msg.sender] -= _value;             // Subtract from the sender's allowance
		totalSupply -= _value;                              // 修改合约代币总数
		Burn(_from, _value);
		return true;
	}
}

contract MyAdvancedToken is owned, token {
	
	uint256 public sellPrice;
	uint256 public buyPrice;

	/* 申请一个数组“freezeAccount”，存储冻结账户的地址和冻结信息 */
	mapping (address => bool) public frozenAccount;

	/* 申请一个事件“FrozenFunds”，提醒客户端发生了冻结 */
	event FrozenFunds(address target, bool frozen);

	/* Initializes contract with initial supply tokens to the creator of the contract */
  	function MyAdvancedToken(
      	uint256 initialSupply,
      	string tokenName,
      	uint8 decimalUnits,
     	string tokenSymbol
	) token (initialSupply, tokenName, decimalUnits, tokenSymbol) {}

	/* Internal transfer, only can be called by this contract */
	function _transfer(address _from, address _to, uint _value) internal {
		require (_to != 0x0);                               // Prevent transfer to 0x0 address. Use burn() instead
		require (balanceOf[_from] > _value);                // Check if the sender has enough
		require (balanceOf[_to] + _value > balanceOf[_to]); // Check for overflows
		require(!frozenAccount[_from]);                     // Check if sender is frozen
		require(!frozenAccount[_to]);                       // Check if recipient is frozen
		balanceOf[_from] -= _value;                         // Subtract from the sender
		balanceOf[_to] += _value;                           // 通知客户端
		Transfer(_from, _to, _value);
	}

	/// @notice Create `mintedAmount` tokens and send it to `target`
	/// @param target Address to receive the tokens
	/// @param mintedAmount the amount of tokens it will receive
	function mintToken(address target, uint256 mintedAmount) onlyOwner {
		balanceOf[target] += mintedAmount;
		totalSupply += mintedAmount;
		Transfer(0, this, mintedAmount);
		Transfer(this, target, mintedAmount);
	}

	/// @notice `freeze? Prevent | Allow` `target` from sending & receiving tokens
	/// @param target Address to be frozen
	/// @param freeze either to freeze it or not(设置0即解锁)
	function freezeAccount(address target, bool freeze) onlyOwner {
		frozenAccount[target] = freeze;
		FrozenFunds(target, freeze);
	}

	/// @notice Allow users to buy tokens for `newBuyPrice` eth and sell tokens for `newSellPrice` eth
	/// @param newSellPrice Price the users can sell to the contract
	/// @param newBuyPrice Price users can buy from the contract
	function setPrices(uint256 newSellPrice, uint256 newBuyPrice) onlyOwner {
		sellPrice = newSellPrice;
		buyPrice = newBuyPrice;
	}

	/// @notice Buy tokens from contract by sending ether(用户购买代币)
	function buy() payable {
		uint amount = msg.value / buyPrice;               // value是用户输入的购买代币支付的以太币数目，amount是根据汇率算出来的代币数目
		_transfer(this, msg.sender, amount);              // 调用_transfer方法执行交易
	}

	/// @notice Sell `amount` tokens to contract(用户向合约出售代币)
	/// @param amount amount of tokens to be sold(出售代币数量)
	function sell(uint256 amount) {
		require(this.balance >= amount * sellPrice);      // 判断合约是否有足够的以太币
		_transfer(msg.sender, this, amount);              // 调用_transfer方法执行交易
		msg.sender.transfer(amount * sellPrice);          // sends ether to the seller. It's important to do this last to avoid recursion attacks
	}
}