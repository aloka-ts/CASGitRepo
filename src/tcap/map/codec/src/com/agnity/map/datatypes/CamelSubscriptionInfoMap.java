package com.agnity.map.datatypes;

import java.util.Collection;

import com.agnity.map.enumdata.SpecificCSIWithdrawMapEnum;

public class CamelSubscriptionInfoMap {
	
	// optional attributes
	
	private OCsiMap ocsi;
	private Collection<OBcsmCamelTDPCriteriaMap> oBcsmCamelTdpCriteriaList;
	private DCsiMap dcsi;
	private TCsiMap tcsi;
	private Collection<TBcsmCamelTDPCriteriaMap> tBcsmCamelTdpCriteriaList;
	private TCsiMap vtcsi;
	private Collection<TBcsmCamelTDPCriteriaMap> vtBcsmCamelTdpCriteriaList;
	private GprsCsiMap gprscsi;
	private SmsCsiMap mosmscsi;
	private SsCsiMap sscsi;
	private MCsiMap mcsi;
	private SpecificCSIWithdrawMap specificCSIDeletedList;
	private SmsCsiMap mtSmsCSI;
	private Collection<MtSmsCamelTDPCriteria> mtSmsCAMELTDPCriteriaList;
	private MgCsiMap mgcsi;
	private OCsiMap oImCsi;
	private Collection<OBcsmCamelTDPCriteriaMap> oImBcsmCamelTdpCriteriaList;
	private DCsiMap dImCsi;
	private TCsiMap vtImCsi;
	private Collection<TBcsmCamelTDPCriteriaMap> vtImBcsmCamelTdpCriteriaList;
	
	/**
	 * @return the ocsi
	 */
	public OCsiMap getOcsi() {
		return ocsi;
	}
	/**
	 * @return the oBcsmCamelTdpCriteriaList
	 */
	public Collection<OBcsmCamelTDPCriteriaMap> getoBcsmCamelTdpCriteriaList() {
		return oBcsmCamelTdpCriteriaList;
	}
	/**
	 * @return the dcsi
	 */
	public DCsiMap getDcsi() {
		return dcsi;
	}
	/**
	 * @return the tcsi
	 */
	public TCsiMap getTcsi() {
		return tcsi;
	}
	/**
	 * @return the tBcsmCamelTdpCriteriaList
	 */
	public Collection<TBcsmCamelTDPCriteriaMap> gettBcsmCamelTdpCriteriaList() {
		return tBcsmCamelTdpCriteriaList;
	}
	/**
	 * @return the vtcsi
	 */
	public TCsiMap getVtcsi() {
		return vtcsi;
	}
	/**
	 * @return the vtBcsmCamelTdpCriteriaList
	 */
	public Collection<TBcsmCamelTDPCriteriaMap> getVtBcsmCamelTdpCriteriaList() {
		return vtBcsmCamelTdpCriteriaList;
	}
	/**
	 * @return the gprscsi
	 */
	public GprsCsiMap getGprscsi() {
		return gprscsi;
	}
	/**
	 * @return the mosmscsi
	 */
	public SmsCsiMap getMoSmscsi() {
		return mosmscsi;
	}
	/**
	 * @return the sscsi
	 */
	public SsCsiMap getSscsi() {
		return sscsi;
	}
	/**
	 * @return the mcsi
	 */
	public MCsiMap getMcsi() {
		return mcsi;
	}
	/**
	 * @return the specificCSIDeletedList
	 */
	public SpecificCSIWithdrawMap getSpecificCSIDeletedList() {
		return specificCSIDeletedList;
	}
	/**
	 * @return the mtSmsCSI
	 */
	public SmsCsiMap getMtSmsCSI() {
		return mtSmsCSI;
	}
	/**
	 * @return the mtSmsCAMELTDPCriteriaList
	 */
	public Collection<MtSmsCamelTDPCriteria> getMtSmsCAMELTDPCriteriaList() {
		return mtSmsCAMELTDPCriteriaList;
	}
	/**
	 * @return the mgcsi
	 */
	public MgCsiMap getMgcsi() {
		return mgcsi;
	}
	/**
	 * @return the oImCsi
	 */
	public OCsiMap getoImCsi() {
		return oImCsi;
	}
	/**
	 * @return the oImBcsmCamelTdpCriteriaList
	 */
	public Collection<OBcsmCamelTDPCriteriaMap> getoImBcsmCamelTdpCriteriaList() {
		return oImBcsmCamelTdpCriteriaList;
	}
	/**
	 * @return the dImCsi
	 */
	public DCsiMap getdImCsi() {
		return dImCsi;
	}
	/**
	 * @return the vtImCsi
	 */
	public TCsiMap getVtImCsi() {
		return vtImCsi;
	}
	/**
	 * @return the vtImBcsmCamelTdpCriteriaList
	 */
	public Collection<TBcsmCamelTDPCriteriaMap> getVtImBcsmCamelTdpCriteriaList() {
		return vtImBcsmCamelTdpCriteriaList;
	}
	/**
	 * @param ocsi the ocsi to set
	 */
	public void setOcsi(OCsiMap ocsi) {
		this.ocsi = ocsi;
	}
	/**
	 * @param oBcsmCamelTdpCriteriaList the oBcsmCamelTdpCriteriaList to set
	 */
	public void setoBcsmCamelTdpCriteriaList(
			Collection<OBcsmCamelTDPCriteriaMap> oBcsmCamelTdpCriteriaList) {
		this.oBcsmCamelTdpCriteriaList = oBcsmCamelTdpCriteriaList;
	}
	/**
	 * @param dcsi the dcsi to set
	 */
	public void setDcsi(DCsiMap dcsi) {
		this.dcsi = dcsi;
	}
	/**
	 * @param tcsi the tcsi to set
	 */
	public void setTcsi(TCsiMap tcsi) {
		this.tcsi = tcsi;
	}
	/**
	 * @param tBcsmCamelTdpCriteriaList the tBcsmCamelTdpCriteriaList to set
	 */
	public void settBcsmCamelTdpCriteriaList(
			Collection<TBcsmCamelTDPCriteriaMap> tBcsmCamelTdpCriteriaList) {
		this.tBcsmCamelTdpCriteriaList = tBcsmCamelTdpCriteriaList;
	}
	/**
	 * @param vtcsi the vtcsi to set
	 */
	public void setVtcsi(TCsiMap vtcsi) {
		this.vtcsi = vtcsi;
	}
	/**
	 * @param vtBcsmCamelTdpCriteriaList the vtBcsmCamelTdpCriteriaList to set
	 */
	public void setVtBcsmCamelTdpCriteriaList(
			Collection<TBcsmCamelTDPCriteriaMap> vtBcsmCamelTdpCriteriaList) {
		this.vtBcsmCamelTdpCriteriaList = vtBcsmCamelTdpCriteriaList;
	}
	/**
	 * @param gprscsi the gprscsi to set
	 */
	public void setGprscsi(GprsCsiMap gprscsi) {
		this.gprscsi = gprscsi;
	}
	/**
	 * @param mosmscsi the smscsi to set
	 */
	public void setMoSmscsi(SmsCsiMap mosmscsi) {
		this.mosmscsi = mosmscsi;
	}
	/**
	 * @param sscsi the sscsi to set
	 */
	public void setSscsi(SsCsiMap sscsi) {
		this.sscsi = sscsi;
	}
	/**
	 * @param mcsi the mcsi to set
	 */
	public void setMcsi(MCsiMap mcsi) {
		this.mcsi = mcsi;
	}
	/**
	 * @param specificCSIDeletedList the specificCSIDeletedList to set
	 */
	public void setSpecificCSIDeletedList(
			SpecificCSIWithdrawMap specificCSIDeletedList) {
		this.specificCSIDeletedList = specificCSIDeletedList;
	}
	/**
	 * @param mtSmsCSI the mtSmsCSI to set
	 */
	public void setMtSmsCSI(SmsCsiMap mtSmsCSI) {
		this.mtSmsCSI = mtSmsCSI;
	}
	/**
	 * @param mtSmsCAMELTDPCriteriaList the mtSmsCAMELTDPCriteriaList to set
	 */
	public void setMtSmsCAMELTDPCriteriaList(
			Collection<MtSmsCamelTDPCriteria> mtSmsCAMELTDPCriteriaList) {
		this.mtSmsCAMELTDPCriteriaList = mtSmsCAMELTDPCriteriaList;
	}
	/**
	 * @param mgcsi the mgcsi to set
	 */
	public void setMgcsi(MgCsiMap mgcsi) {
		this.mgcsi = mgcsi;
	}
	/**
	 * @param oImCsi the oImCsi to set
	 */
	public void setoImCsi(OCsiMap oImCsi) {
		this.oImCsi = oImCsi;
	}
	/**
	 * @param oImBcsmCamelTdpCriteriaList the oImBcsmCamelTdpCriteriaList to set
	 */
	public void setoImBcsmCamelTdpCriteriaList(
			Collection<OBcsmCamelTDPCriteriaMap> oImBcsmCamelTdpCriteriaList) {
		this.oImBcsmCamelTdpCriteriaList = oImBcsmCamelTdpCriteriaList;
	}
	/**
	 * @param dImCsi the dImCsi to set
	 */
	public void setdImCsi(DCsiMap dImCsi) {
		this.dImCsi = dImCsi;
	}
	/**
	 * @param vtImCsi the vtImCsi to set
	 */
	public void setVtImCsi(TCsiMap vtImCsi) {
		this.vtImCsi = vtImCsi;
	}
	/**
	 * @param vtImBcsmCamelTdpCriteriaList the vtImBcsmCamelTdpCriteriaList to set
	 */
	public void setVtImBcsmCamelTdpCriteriaList(
			Collection<TBcsmCamelTDPCriteriaMap> vtImBcsmCamelTdpCriteriaList) {
		this.vtImBcsmCamelTdpCriteriaList = vtImBcsmCamelTdpCriteriaList;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CAMELSubscriptionInfoMap [ocsi=" + ocsi + "\n"
				+ ", oBcsmCamelTdpCriteriaList=" + oBcsmCamelTdpCriteriaList+ "\n"
				+ ", dcsi=" + dcsi + ", tcsi=" + tcsi+ "\n"
				+ ", tBcsmCamelTdpCriteriaList=" + tBcsmCamelTdpCriteriaList+ "\n"
				+ ", vtcsi=" + vtcsi + ", vtBcsmCamelTdpCriteriaList="+ "\n"
				+ vtBcsmCamelTdpCriteriaList + ", gprscsi=" + gprscsi+ "\n"
				+ ", mosmscsi=" + mosmscsi + ", sscsi=" + sscsi + ", mcsi=" + mcsi+ "\n"
				+ ", specificCSIDeletedList=" + specificCSIDeletedList+ "\n"
				+ ", mtSmsCSI=" + mtSmsCSI + ", mtSmsCAMELTDPCriteriaList="+ "\n"
				+ mtSmsCAMELTDPCriteriaList + ", mgcsi=" + mgcsi + ", oImCsi="+ "\n"
				+ oImCsi + ", oImBcsmCamelTdpCriteriaList="+ "\n"
				+ oImBcsmCamelTdpCriteriaList + ", dImCsi=" + dImCsi+ "\n"
				+ ", vtImCsi=" + vtImCsi + ", vtImBcsmCamelTdpCriteriaList="+ "\n"
				+ vtImBcsmCamelTdpCriteriaList + "]";
	}
	

	
	
}