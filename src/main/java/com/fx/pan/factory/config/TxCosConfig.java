package com.fx.pan.factory.config;

import com.fx.pan.factory.domain.TxCos;

/**
 * @author leaving
 * @date 2022/3/19 16:37
 * @version 1.0
 */

public class TxCosConfig {

    private TxCos cos = new TxCos();

    public TxCosConfig() {
    }

    public TxCos getCos() {
        return this.cos;
    }

    public void setCos(final TxCos oss) {
        this.cos = oss;
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
                Object this$oss = this.getCos();
                Object other$oss = other.getCos();
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
        Object $oss = this.getCos();
         result = result * 59 + ($oss == null ? 43 : $oss.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "AliyunConfig(oss=" + this.getCos() + ")";
    }
}
