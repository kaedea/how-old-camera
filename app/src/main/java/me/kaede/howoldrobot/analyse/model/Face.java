package me.kaede.howoldrobot.analyse.model;

/**
 * Created by kaede on 2015/5/23.
 */
public class Face {
    public int faceId;
    public FaceRectangle faceRectangle;
    public Attributes attributes;
    public  Face(){
        faceRectangle = new FaceRectangle();
        attributes = new Attributes();
    }

    @Override
    public String toString() {
        return "Face{" +
                "faceId=" + faceId +
                ", faceRectangle=" + faceRectangle +
                ", attributes=" + attributes +
                '}';
    }
}



/*
        [
        {
        "faceId": null,
        "faceRectangle": {
        "top": 181,
        "left": 568,
        "width": 37,
        "height": 37
        },
        "attributes": {
        "gender": "Female",
        "age": 25
        }
        },
        {
        "faceId": null,
        "faceRectangle": {
        "top": 184,
        "left": 490,
        "width": 35,
        "height": 35
        },
        "attributes": {
        "gender": "Male",
        "age": 26
        }
        },
        {
        "faceId": null,
        "faceRectangle": {
        "top": 159,
        "left": 326,
        "width": 35,
        "height": 35
        },
        "attributes": {
        "gender": "Male",
        "age": 44.5
        }
        },
        {
        "faceId": null,
        "faceRectangle": {
        "top": 184,
        "left": 635,
        "width": 33,
        "height": 33
        },
        "attributes": {
        "gender": "Male",
        "age": 20.5
        }
        },
        {
        "faceId": null,
        "faceRectangle": {
        "top": 184,
        "left": 254,
        "width": 33,
        "height": 33
        },
        "attributes": {
        "gender": "Female",
        "age": 24
        }
        },
        {
        "faceId": null,
        "faceRectangle": {
        "top": 187,
        "left": 158,
        "width": 32,
        "height": 32
        },
        "attributes": {
        "gender": "Female",
        "age": 25
        }
        },
        {
        "faceId": null,
        "faceRectangle": {
        "top": 187,
        "left": 400,
        "width": 31,
        "height": 31
        },
        "attributes": {
        "gender": "Female",
        "age": 24
        }
        }
        ]
*/
