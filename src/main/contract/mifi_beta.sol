pragma solidity ^0.4.13;

/*********************** Qlc����������ܺ�Լ ***********************/
/// @title Qlc�����ֵ�
contract QlcDataDictionary {
	
	mapping (address => uint256) public balanceOf;	// ��ַ�͵�ַ�����е�QLC��������

	// ����
	function increase(address _target, uint256 _value) returns (bool success) {
		balanceOf[_target] += _value;
		return true;
	}
	
	// ����
	function reduce(address _target, uint256 _value) returns (bool success) {
		require (_value < balanceOf[_target]);     // �������Ƿ��㹻
		balanceOf[_target] -= _value;
		return true;
	}
}

/// @title QLC���׼�¼
contract QlcRecord {

	// �Զ������ͣ�QLC���׼�¼
	struct QlcRecord {
    	address record_address;		// ��¼������ַ
		uint record_type;   		// ��¼����(0_����,1_����)
		uint amount;   				// QLC����
		uint time;   				// ����ʱ��(����)
		string description;		// ����
	}
	
    // һ���洢`QlcRecord`�ṹ�Ķ�̬����
    QlcRecord[] private qlcRecords;
	
    // �洢�豸��������
    function qlcRecord_add(address record_address, uint record_type, uint amount, uint time, string description) {   		
   		// �洢
 		qlcRecords.push(QlcRecord({
            record_address: record_address,
            record_type: record_type,
            amount: amount,
            time: time,
            description: description
        }));
    }
}

/// @title �豸������¼
contract DeviceFlowRecordContract is QlcRecord, QlcDataDictionary {

	// �Զ������ͣ��豸����
	struct DeviceFlowRecord {
		bytes32 imei;   			// �豸��ţ�32�ֽڣ�
    	address user_address;		// �豸�����û���Ӧ�ĵ�ַ
		uint used_flow;   			// ��ʹ������(M)
		uint time;   				// ����ʱ��(����)
		uint flow_ratio;   			// QLC����������(1M��Ӧ��QLC*1000)
		uint time_ratio;   			// QLC��ʱ�����(1���Ӷ�Ӧ��QLC*1000)
		uint flow_sign;				// �������(M)
		uint time_sign;				// ʱ����(����)
	}
	
    // һ���洢`DeviceFlowRecord`�ṹ�Ķ�̬����
    DeviceFlowRecord[] private deviceFlowRecords;
	
    // �洢�豸��������
    function deviceFlowRecord_add(bytes32 imei, address user_address, uint used_flow, uint time, uint flow_ratio, uint time_ratio, uint flow_sign, uint time_sign) {
       
        DeviceFlowRecord deviceFlowRecord = null;  // ���Ϊ�棬���ʾ���豸�Ѿ��洢ֻ��Ҫ�޸ġ�
        // ѭ��deviceFlowRecords���ݣ�����豸�Ѵ������޸Ķ�������������򴴽�һ��DeviceFlowRecord������ӵ�deviceFlowRecords
        for (uint i=0; i<deviceFlowRecords.length; i++) {
        	if (deviceFlowRecords[i].imei == imei) {
        		deviceFlowRecord = deviceFlowRecords[i];
        		break;
       		}
   	 	}
   	 	// ������μ�¼ʱ��֮�����ʱ���������QLCʹ����
   	 	if (deviceFlowRecord != null) {
   	 		uint flow_qlc = 0;	// ��������QLC
   	 		uint time_qlc = 0;	// ʱ������QLC
   	 		if ((used_flow - deviceFlowRecord.used_flow) > flow_sign) {
   	 			uint flow_qlc = (used_flow - deviceFlowRecord.used_flow) * flow_ratio;
   	 		}
   	 		if ((time - deviceFlowRecord.time) > time_sign) {
   	 			uint time_qlc = (time - deviceFlowRecord.time) * time_ratio;
   	 		}
   	 		uint qlc = (flow_qlc + time_qlc);
   	 		if (qlc > 0) {
   	 			// ���������ʲ�����QLC����
   	 			reduce(user_address, qlc);
   	 			// ��¼�����ʲ����׼�¼
   	 			qlcRecord_add(user_address, 1, qlc, time, "flow QLC:" + flow_qlc + ",time QLC" + time_qlc);
   	 		}
   		}
   		
   		// �洢
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

/*********************** ����������ܺ�Լ ***********************/
/* �����Լӵ���� */
contract owned {
	
    /* ��Լӵ���ߵ�ַ */
    address public owner;

    function owned() {
        owner = msg.sender;
    }

    modifier onlyOwner {
        require(msg.sender == owner);
        _;
    }
	
	/* ��Լ����Ȩת�� */
    function transferOwnership(address newOwner) onlyOwner {
        owner = newOwner;
    }
}

contract tokenRecipient { 
	function receiveApproval(address from, uint256 value, address token, bytes extraData); 
}

/* ������Լ */
contract token { 
	
	string public name; 				// ��Լ����
	string public symbol; 				// ��Լ������ĸ�ı���
	uint8 public decimals; 				// С���㱣��λ��
	uint256 public totalSupply;			// �ܷ��д�������
	
	mapping (address => uint256) public balanceOf;	// ��ַ�͵�ַ�����еĴ�����������
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

	/* �ڲ�ת�ã�ֻ���ɱ���Լ���� */
	function _transfer(address _from, address _to, uint _value) internal {
		require (_to != 0x0);                               // Prevent transfer to 0x0 address. Use burn() instead
		require (balanceOf[_from] > _value);                // Check if the sender has enough
		require (balanceOf[_to] + _value > balanceOf[_to]); // Check for overflows
		balanceOf[_from] -= _value;                         // Subtract from the sender
		balanceOf[_to] += _value;                           // Add the same to the recipient
		Transfer(_from, _to, _value);
	}

	/// @notice �ӵ����ߵĵ�ַ��'_to'��ַ���ʹ���
	/// @param _to ���ҽ����ߵ�ַ
	/// @param _value ���͵�����
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
		// ���û��˻�����QLC�����˻�
		transferFrom(_from, _to, _value);
		// �û������ʲ�����
		increase(_from, _value);
		// ��¼�����ʲ����׼�¼
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

	/// @notice �Ӻ�Լ�����ߵ�ַ�ϼ��ٴ��ҷ��е�����
	/// @param _value ���ٴ��ҵ�����
	function burn(uint256 _value) returns (bool success) {
		require (balanceOf[msg.sender] > _value);             // �жϺ�Լ�����ߵĴ��������Ƿ��㹻
		balanceOf[msg.sender] -= _value;                      // ���ٺ�Լ�����ߵĴ�������
		totalSupply -= _value;                                // �޸ĺ�Լ��������
		Burn(msg.sender, _value);
		return true;
	}
	
	/// @notice ��ָ����ַ�ϼ��ٴ��ҷ��е�����
	/// @param _from ָ���ĵ�ַ
	/// @param _value ���ٴ��ҵ�����
	function burnFrom(address _from, uint256 _value) returns (bool success) {
		require(balanceOf[_from] >= _value);                // ���Ŀ���ַ����Ƿ��㹻
		require(_value <= allowance[_from][msg.sender]);    // Check allowance
		balanceOf[_from] -= _value;                         // ����Ŀ���ַ�Ĵ�������
		allowance[_from][msg.sender] -= _value;             // Subtract from the sender's allowance
		totalSupply -= _value;                              // �޸ĺ�Լ��������
		Burn(_from, _value);
		return true;
	}
}

contract MyAdvancedToken is owned, token {
	
	uint256 public sellPrice;
	uint256 public buyPrice;

	/* ����һ�����顰freezeAccount�����洢�����˻��ĵ�ַ�Ͷ�����Ϣ */
	mapping (address => bool) public frozenAccount;

	/* ����һ���¼���FrozenFunds�������ѿͻ��˷����˶��� */
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
		balanceOf[_to] += _value;                           // ֪ͨ�ͻ���
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
	/// @param freeze either to freeze it or not(����0������)
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

	/// @notice Buy tokens from contract by sending ether(�û��������)
	function buy() payable {
		uint amount = msg.value / buyPrice;               // value���û�����Ĺ������֧������̫����Ŀ��amount�Ǹ��ݻ���������Ĵ�����Ŀ
		_transfer(this, msg.sender, amount);              // ����_transfer����ִ�н���
	}

	/// @notice Sell `amount` tokens to contract(�û����Լ���۴���)
	/// @param amount amount of tokens to be sold(���۴�������)
	function sell(uint256 amount) {
		require(this.balance >= amount * sellPrice);      // �жϺ�Լ�Ƿ����㹻����̫��
		_transfer(msg.sender, this, amount);              // ����_transfer����ִ�н���
		msg.sender.transfer(amount * sellPrice);          // sends ether to the seller. It's important to do this last to avoid recursion attacks
	}
}