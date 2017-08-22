/**
 * 金额格式判断
 * @param o
 * @returns
 */
function isMoney(o) {
	return /^\+?[0-9]{1,8}(\.[0-9]{1,2})?$/.test(o);
}

/**
 * 判断是否为空
 * @param o
 * @returns {Boolean}
 */
function isEmpty(o){
	if(o==null||o=='undefined'||o==''){
		return true;
	}else{
		return false;
	}
}

/**
 * 手机号码格式判断(大陆)
 * @param o
 * @returns
 */
function isPhone(o) {
	return /^(13|14|15|16|18)\d{9}$/.test(o);
}

/**
 * 手机号码格式判断(台湾)
 * @param o
 * @returns
 */
function isTWPhone(o) {
	return /^(09)\d{8}$/.test(o);
}

/**
 * 全世界
 * @param o
 * @returns
 */
function isWordPhone(o) {
	return /^[\d\-]{8,20}$/.test(o);
}

/**
 * 邮箱格式判断
 * @param o
 * @returns
 */
function isEmail(o) {
	return /^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/.test(o);
}

/**
 * 多邮箱格式判断
 * @param o
 * @returns
 */
$.validator.addMethod("emails", function(value,element){

	if (!this.optional(element)) {
		if (!isEmpty(value)) {
			var emailArr = value.split(',');
			var len = emailArr.length;
			var reg = /^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/;
			for (var i=0; i<len; i++) {
				if (!isEmail(emailArr[i])) {
					return false;
				}
			}
		}
	}
	
    return true;
},"<font color='#E47068'>邮箱格式错误</font>");

/**
 * 多手机号码格式判断
 * @param o
 * @returns
 */
$.validator.addMethod("mobiles", function(value,element){

	if (!this.optional(element)) {
		if (!isEmpty(value)) {
			var phoneArr = value.split(',');
			var len = phoneArr.length;
			for (var i=0; i<len; i++) {
				if (!isPhone(phoneArr[i])) {
					return false;
				}
			}
		}
	}
	
    return true;
},"<font color='#E47068'>手机号码格式错误</font>");

/**
 * 取系统当前时间 格式：yyyy-MM-dd HH:mm:ss
 * @param o
 * @returns
 */
function getTime() {
	var date = new Date();  
	this.year = date.getFullYear();  
	this.month = (date.getMonth()+1) < 10 ? ("0" + (date.getMonth()+1)) : (date.getMonth()+1);  
	this.date = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
	this.hour = date.getHours() < 10 ? "0" + date.getHours() : date.getHours();  
	this.minute = date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes();  
	this.second = date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();  
	var currentTime = this.year + "-" + this.month + "-" + this.date + " " + this.hour + ":" + this.minute + ":" + this.second;  
	return currentTime;
}



