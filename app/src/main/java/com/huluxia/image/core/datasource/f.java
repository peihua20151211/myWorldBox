package com.huluxia.image.core.datasource;

import com.huluxia.framework.base.utils.Objects;
import com.huluxia.framework.base.utils.Preconditions;
import com.huluxia.framework.base.utils.Supplier;
import java.util.List;
import javax.annotation.concurrent.ThreadSafe;
import org.apache.tools.ant.taskdefs.optional.j2ee.HotDeploymentTool;

@ThreadSafe
/* compiled from: FirstAvailableDataSourceSupplier */
public class f<T> implements Supplier<c<T>> {
    private final List<Supplier<c<T>>> zx;

    private f(List<Supplier<c<T>>> dataSourceSuppliers) {
        Preconditions.checkArgument(!dataSourceSuppliers.isEmpty(), "List of suppliers is empty!");
        this.zx = dataSourceSuppliers;
    }

    public static <T> f<T> k(List<Supplier<c<T>>> dataSourceSuppliers) {
        return new f(dataSourceSuppliers);
    }

    public c<T> get() {
        return new a(this);
    }

    public int hashCode() {
        return this.zx.hashCode();
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof f)) {
            return false;
        }
        return Objects.equal(this.zx, ((f) other).zx);
    }

    public String toString() {
        return Objects.toStringHelper(this).add(HotDeploymentTool.ACTION_LIST, this.zx).toString();
    }
}
