package se.technipelago.sequence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;
import java.io.Serializable;

/**
 * Entity for persisting sequence definitions.
 */
@Entity
public class SequenceDefinition implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private long version;

    @Column(nullable = false)
    private Long tenantId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = true, length = 100)
    private String format;

    protected SequenceDefinition() {
    }

    public SequenceDefinition(Long tenantId, String name) {
        this.tenantId = tenantId;
        this.name = name;
    }

    public SequenceDefinition(Long tenantId, String name, String format) {
        this.tenantId = tenantId;
        this.name = name;
        this.format = format;
    }

    public Long getId() {
        return id;
    }

    public long getVersion() {
        return version;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return getName();
    }
}
