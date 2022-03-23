package com.fx.pan.factory.config;

import com.fx.pan.factory.domain.TxCos;

/**
 * @Author leaving
 * @Date 2022/3/19 16:37
 * @Version 1.0
 */

public class TxCosConfig {

    private TxCos oss = new TxCos();

    public TxCosConfig() {
    }

    public TxCos getOss() {
        return this.oss;
    }

    public void setOss(final TxCos oss) {
        this.oss = oss;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof TxCosConfig)) {
            return false;
        } else {
            TxCosConfig other = (TxCosConfig)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$oss = this.getOss();
                Object other$oss = other.getOss();
                if (this$oss == null) {
                    if (other$oss != null) {
                        return false;
                    }
                } else if (!this$oss.equals(other$oss)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof TxCosConfig;
    }

    @Override
    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object $oss = this.getOss();
         result = result * 59 + ($oss == null ? 43 : $oss.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "AliyunConfig(oss=" + this.getOss() + ")";
    }
}
