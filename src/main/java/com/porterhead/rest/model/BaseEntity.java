package com.porterhead.rest.model;

import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.util.Assert;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.util.Date;
import java.util.UUID;

/**
 * Base class for all JPA Entities
 *
 * @author : Iain Porter
 */
@MappedSuperclass
public abstract class BaseEntity extends AbstractPersistable<Long> {

    @Version
    private int version;

    /**
     *  All objects will have a unique UUID which allows for the decoupling from DB generated ids
     *
     */
    @Column(length=36)
    private String uuid;

    private Date timeCreated;

    public BaseEntity() {
        this(UUID.randomUUID());
    }

    public BaseEntity(UUID guid) {
        Assert.notNull(guid, "UUID is required");
        setUuid(guid.toString());
        this.timeCreated = new Date();
    }

    public UUID getUuid() {
        return UUID.fromString(uuid);
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int hashCode() {
        return getUuid().hashCode();
    }

    /**
     * In most instances we can rely on the UUID to identify the object.
     * Subclasses may want a user friendly identifier for constructing easy to read urls
     *
     * <code>
     *    /user/1883c578-76be-47fb-a5c1-7bbea3bf7fd0 using uuid as the identifier
     *
     *    /user/jsmith using the username as the identifier
     *
     * </code>
     *
     * @return Object unique identifier for the object
     */
    public Object getIdentifier() {
        return getUuid().toString();
    }

    public int getVersion() {
        return version;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

}
