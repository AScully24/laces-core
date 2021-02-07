package com.laces.core.security.component.user

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.laces.core.jpa.HasId
import java.io.Serializable
import javax.persistence.*
import javax.persistence.GenerationType.TABLE

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@JsonTypeInfo(visible = true, use = JsonTypeInfo.Id.CLASS,  property = "classType")
@kotlinx.serialization.Serializable
abstract class AdditionalInfo : HasId, Serializable {

    @Id
    @GeneratedValue(strategy = TABLE, generator = "ADDITIONAL_INFO_GEN")
    @TableGenerator(name = "ADDITIONAL_INFO_GEN",
            table = "ID_GENERATOR",
            pkColumnName = "GEN_NAME",
            valueColumnName = "GEN_VAL",
            allocationSize = 1)
    override var id: Long? = null

    abstract fun toDto() : Map<String, Any>

}