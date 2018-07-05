package com.jees.test.entity;

import com.jees.webs.entity.SuperMenu;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.directwebremoting.annotations.DataTransferObject;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@DataTransferObject
@Entity
@Table( name = "js_menu" , catalog = "jees_common" )
public class Menu extends SuperMenu< Integer, Role > {
    @Override
    public SuperMenu build(){
        return new Menu();
    }
}
