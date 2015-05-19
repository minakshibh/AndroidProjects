package com.zira.modal;

public class ContactBean {
	String number = null;
    String name = null;
    boolean selected = false;
    
    public ContactBean(String name) {
    this.name = name;
    }
    
     public ContactBean(String name, String num) {
     this.name = name;
     this.number = num;
    }
     
    public ContactBean(String num, String name, boolean selected) {
              super();
              this.number = num;
              this.name = name;
              this.selected = selected;
    }

        public String getNum() {
            return number;
        }
        public void setNum(String num) {
            this.number = num;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public boolean isSelected() {
            return selected;
        }
        public void setSelected(boolean selected) {
            this.selected = selected;
        }
}



