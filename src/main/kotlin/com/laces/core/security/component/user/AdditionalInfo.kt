package com.laces.core.security.component.user

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.laces.core.jpa.HasId
import javax.persistence.*
import javax.persistence.GenerationType.TABLE

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "classType")
abstract class AdditionalInfo : HasId {

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