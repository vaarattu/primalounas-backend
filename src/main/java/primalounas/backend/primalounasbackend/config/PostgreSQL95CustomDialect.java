package primalounas.backend.primalounasbackend.config;

import com.vladmihalcea.hibernate.type.array.StringArrayType;
import org.hibernate.dialect.PostgreSQL95Dialect;

public class PostgreSQL95CustomDialect extends PostgreSQL95Dialect {

    public PostgreSQL95CustomDialect() {
        super();
        this.registerHibernateType(2003, StringArrayType.class.getName());
    }

}