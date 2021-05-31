package com.grievancesystem.speakout.models;

public class MapPlaces {

        private String name;
        private int img;

        public MapPlaces(String name, int img) {
            this.name = name;
            this.img = img;
        }

        public MapPlaces(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public int getImg() {
            return img;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setImg(int img) {
            this.img = img;
        }

}
