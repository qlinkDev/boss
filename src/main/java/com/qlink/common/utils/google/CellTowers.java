package main.java.com.qlink.common.utils.google;

/**
 * 移动电话基站对象
 * 
 * @author wangpeng
 *
 */
public class CellTowers {

	/**
	 * （必填） 小区的唯一标识符。在 GSM 上，这就是小区 ID (CID)； CDMA 网络使用的是基站 ID (BID)。 WCDMA 网络使用
	 * UTRAN/GERAN 小区标识 (UC-Id)，这是一个 32 位的值， 由无线网络控制器 (RNC) 和小区 ID 连接而成。在 WCDMA
	 * 网络中， 如果只指定 16 位的小区 ID 值，返回的结果可能会不准确。
	 */
	private String cellId;

	/**
	 * （必填）：GSM 和 WCDMA 网络的位置区域代码 (LAC)。CDMA 网络的网络 ID (NID)。
	 */
	private String locationAreaCode;

	/**
	 * （必填）：移动电话基站的移动国家代码 (MCC)。
	 */
	private String mobileCountryCode;

	/**
	 * （必填）：移动电话基站的移动网络代码。对于 GSM 和 WCDMA，这就是 MNC；CDMA 使用的是系统 ID (SID)。
	 */
	private String mobileNetworkCode;

	public String getCellId() {
		return cellId;
	}

	public void setCellId(String cellId) {
		this.cellId = cellId;
	}

	public String getLocationAreaCode() {
		return locationAreaCode;
	}

	public void setLocationAreaCode(String locationAreaCode) {
		this.locationAreaCode = locationAreaCode;
	}

	public String getMobileCountryCode() {
		return mobileCountryCode;
	}

	public void setMobileCountryCode(String mobileCountryCode) {
		this.mobileCountryCode = mobileCountryCode;
	}

	public String getMobileNetworkCode() {
		return mobileNetworkCode;
	}

	public void setMobileNetworkCode(String mobileNetworkCode) {
		this.mobileNetworkCode = mobileNetworkCode;
	}

}
