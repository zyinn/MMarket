package com.sumscope.optimus.moneymarket.gatewayinvoke.model;

/**
 * Created by Administrator on 2016/11/16.
 */
public class QbUserId {
        private String id;//id
        private String userid;//userid是qmid
        private String accountid;//accountid是qbid

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getAccountid() {
            return accountid;
        }

        public void setAccountid(String accountid) {
            this.accountid = accountid;
        }
}
