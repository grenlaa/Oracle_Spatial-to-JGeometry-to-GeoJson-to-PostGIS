/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package converter_JGeometry;

import oracle.spatial.geometry.JGeometry;

/**
 *
 *
 */
public class converter_to_GeoJSON {

    /*
            !!!!!!!!!!!!!!!!!
            objs_type[i]---SDO_STARTING_OFFSET — указывает смещение в массиве SDO_ORDINATES, где хранится первая ордината для этого элемента (начиная с 1).
            objs_type[i + 1]---SDO_ETYPE
            objs_type[i + 2]---SDO_INTERPRETATION
            !!!!!!!!!!!!!!!!!  
     */
    public static String convert(JGeometry JGeometry) throws Exception {
        return convert(JGeometry, "base");
    }

    public static String convert(JGeometry JGeometry, String geom_type) throws Exception {

        if (JGeometry == null) {
            return null;
        }

        int GEOMETRY_TYPE = JGeometry.getType();

        if (!geom_type.equalsIgnoreCase("base")) {
            GEOMETRY_TYPE = get_bgt(geom_type);
        }

        String str = "";
        if (GEOMETRY_TYPE == 99) {
            return null;
        }
        if (GEOMETRY_TYPE == 0) {
        }
        if (GEOMETRY_TYPE == 1) {
            str = "{ \"type\": \"Point\", \"coordinates\": " + elem_to_json(JGeometry, geom_type) + " }";
        }
        if (GEOMETRY_TYPE == 2) {
            str = "{ \"type\": \"Linestring\", \"coordinates\":" + elem_to_json(JGeometry, geom_type) + "}";
        }
        if (GEOMETRY_TYPE == 3) {
            str = "{ \"type\": \"Polygon\", \"coordinates\":" + elem_to_json(JGeometry, geom_type) + "}";
        }
        if (GEOMETRY_TYPE == 4) {
            str = "{ \"type\": \"GeometryCollection\", \"geometries\":[" + elem_to_json(JGeometry, geom_type) + "]}";
        }
        if (GEOMETRY_TYPE == 5) {
            str = "{ \"type\": \"MultiPoint\", \"coordinates\":[" + elem_to_json(JGeometry, geom_type) + "]}";
        }
        if (GEOMETRY_TYPE == 6) {
            str = "{ \"type\": \"MultiLinestring\", \"coordinates\":[" + elem_to_json(JGeometry, geom_type) + "]}";
        }
        if (GEOMETRY_TYPE == 7) {
//            return null;
            str = "{ \"type\": \"MultiPolygon\", \"coordinates\":[" + elem_to_json(JGeometry, geom_type) + "]}";
        }
//        if (GEOMETRY_TYPE == 8) {
//        }
//        if (GEOMETRY_TYPE == 9) {
//        }

        return str;
    }

    static private String polygon_get_coords_by_SDO_INTERPRETATION(double[] ordinates, int dimensions, int interp) throws Exception {
        String str = "";
        if (interp == 1) {
            //простой многоугольник 
            str = "[" + get_coords(ordinates, dimensions) + "]";
        }
        if (interp == 2) {
            /*undefined 
                    
                    Многоугольник, состоящий из соединённых между собой дуг окружности, замыкающихся на себя.
                    Конечная точка последней дуги совпадает с начальной точкой первой дуги.
                    Каждая дуга окружности описывается с помощью трёх координат: начальная точка дуги,
                    любая точка на дуге и конечная точка дуги. Координаты точки, обозначающей 
                    конец одной дуги и начало следующей дуги, не повторяются. Например, для описания многоугольника,
                    состоящего из двух соединённых дуг окружности, используются пять координат.
                    Точки 1, 2 и 3 определяют первую дугу, а точки 3, 4 и 5 определяют вторую дугу.
                    Координаты точек 1 и 5 должны быть одинаковыми (допуск не учитывается), а точка 3 не повторяется.
             */
        }
        if (interp == 3) {
            /*undefined 
                    
                    Тип «Прямоугольник» (иногда называемый оптимизированным прямоугольником).
                    Ограничивающий прямоугольник, для описания которого требуются только две точки: левая
                    нижняя и правая верхняя. Тип «Прямоугольник» можно использовать с геодезическими или
                    негеодезическими данными. Однако при работе с геодезическими данными используйте этот
                    тип только для создания окна запроса (не для хранения объектов в базе данных).
             */
        }
        if (interp == 4) {
            /*undefined
                    
                    Тип окружности. Описывается тремя различными не совпадающими точками, расположенными на окружности.
             */
        }
        return str;

    }

    static private String LineString_get_coords_by_SDO_INTERPRETATION(double[] ordinates, int dimensions, int interp) throws Exception {
        String str = "";
        if (interp == 1) {
            str = "[" + get_coords(ordinates, dimensions) + "]";
            //простая кривая линия
        }
        if (interp == 2) {
            str = "[" + get_coords(ordinates, dimensions) + "]";
            /*
            если хотите усраться то аппроксимируйте изогнутые геометрии как линейные.
            undefined
             */
        }

        if (interp == 4) {
            /*undefined
                  Compound line string
             */
        }
        return str;

    }

    static private String elem_to_json(JGeometry JGeometry, String geom_type) throws Exception {

        String str = "";
        String str_elem = "";
        String str_elem_end = "";

        int element_collection = JGeometry.getType();
        if (!geom_type.equalsIgnoreCase("base")) {
            element_collection = get_bgt(geom_type);
        }

        if (element_collection == 4) {
            str_elem_end = "}";
        }

        int[] objs_type = get_eleminfo_revised(JGeometry.getElemInfo(), geom_type, JGeometry.getType());

        Object[] objs_ordinats = JGeometry.getOrdinatesOfElements();

        int y = 0;

        //количество элементов в элементе типа(SDO_ETYPE)1005|2005|1006|2006
        int n = 0;

        boolean start_n = false;

        for (int i = 0; i < objs_type.length; i = i + 3, y = y + 1) {

            if (str.length() > 0 && start_n == false) {
                str = str + ",";
            }
            if (n > 0) {
                start_n = false;
                n = n - 1;
            }

            if (objs_type[i + 1] == 1) {
                if (element_collection == 4) {
                    str_elem = "{\"type\": \"Point\",\n \"coordinates\": ";
                }

                if (objs_type[i + 2] == 1) {
                    str = str + str_elem + get_coords((double[]) objs_ordinats[y], JGeometry.getDimensions()) + str_elem_end;
                }
                if (objs_type[i + 2] > 1) {
                    /*не нашел или не искал*/
                }
            }
            if (objs_type[i + 1] == 2) {
                if (element_collection == 4) {
                    str_elem = "{\"type\": \"LineString\",\n \"coordinates\": ";
                }
                str = str + str_elem + LineString_get_coords_by_SDO_INTERPRETATION((double[]) objs_ordinats[y], JGeometry.getDimensions(), objs_type[i + 2]) + str_elem_end;
            }

            if (objs_type[i + 1] == 1003) {
                if (element_collection == 4) {
                    str_elem = "{\"type\": \"Polygon\",\n \"coordinates\": ";
                }
                /*Так как полигон могут иметь отверстия, то есть несколько колец. Выделяем их в отдельный массив*/
                str = str + str_elem + "[";
                str = str + polygon_get_coords_by_SDO_INTERPRETATION((double[]) objs_ordinats[y], JGeometry.getDimensions(), objs_type[i + 2]);
            }
            if (objs_type[i + 1] == 2003) {
                str = str + polygon_get_coords_by_SDO_INTERPRETATION((double[]) objs_ordinats[y], JGeometry.getDimensions(), objs_type[i + 2]);
            }

            if (objs_type[i + 1] == 4) {

                str = str + "[" + get_coords((double[]) objs_ordinats[y], JGeometry.getDimensions()) + "]";
                /*
                    Составная линейная строка, в которой некоторые вершины соединены отрезками прямых
                    линий, а некоторые — дугами окружностей. Значение n в столбце «Интерпретация»
                    указывает количество смежных подэлементов, из которых состоит линейная строка.
                 */

//                for(){}
            }
            if (objs_type[i + 1] == 1005 || objs_type[i + 1] == 2005) {
                start_n = true;
                str = str + "[";
                n = objs_type[i + 2] + 1;
                y = y - 1;
//                 str = str + "[" + get_coords((double[]) objs_ordinats[y], JGeometry.getDimensions()) + "]";
                /*
                какая то дичь
                на примере:
                3003||0||NULL|NULL|NULL||1|1005|2|1|2|2|7|2|2||55.6|38.03|0|51.99|41.69|0|48.32|38.03|0|51.99|34.36|0|55.65|38.03|0||
                
         
                Составной многоугольник, в котором некоторые вершины соединены отрезками прямых линий,
                а некоторые — дугами окружностей. Значение n в столбце «Интерпретация» указывает количество
                смежных подэлементов, из которых состоит многоугольник.
                 */
            }
            if (objs_type[i + 1] == 1006 || objs_type[i + 1] == 2006) {
                start_n = true;
                str = str + "[";
                n = objs_type[i + 2] + 1;
                y = y - 1;
            }

            ////////////////////////////////////////////
            if (n == 1) {
                str = str + "]";
                n = 0;
            }
            ///////////////////////////////////////////

            if (objs_type.length > i + 3) {
                if (objs_type[i + 4] == 1003) {
                    str = str + "]" + str_elem_end;
                }
            } else {
                if (objs_type[i + 1] == 1003 || objs_type[i + 1] == 2003) {
                    str = str + "]" + str_elem_end;
                }
            }

        }
        if (element_collection == 1) {
            str = "{" + str + "}";
        }
        return str;
    }

    static String get_coords(double[] ordinates, int dimensions) {
        String str = "";
        for (int i = 0; i < ordinates.length; i = i + dimensions) {
            if (str.length() > 0) {
                str = str + ",";
            }
            str = str + "[";
            for (int j = 0; j < dimensions; j = j + 1) {
                if (j != 0) {
                    str = str + ",";
                }
                str = str + String.valueOf(ordinates[i + j]);
            }
            str = str + "]";
        }
        return str;
    }

    private static int get_bgt(String geom_type) {

        if (geom_type.equalsIgnoreCase("base")) {
            return 0;
        }
        if (geom_type.equalsIgnoreCase("Linestring")) {
            return 2;
        }
        if (geom_type.equalsIgnoreCase("Polygon")) {
            return 3;
        }
        if (geom_type.equalsIgnoreCase("GeometryCollection")) {
            return 4;
        }
        if (geom_type.equalsIgnoreCase("MultiPoint")) {
            return 5;
        }
        if (geom_type.equalsIgnoreCase("MultiLinestring")) {
            return 6;
        }
        if (geom_type.equalsIgnoreCase("MultiPolygon")) {
            return 7;
        }

        return 99;
    }

    private static int[] get_eleminfo_revised(int[] objs_type, String geom_type, int jg_type) {

        if (geom_type.equalsIgnoreCase("base")) {
            return objs_type;
        }

        for (int i = 0; i < objs_type.length; i = i + 3) {
            if (geom_type.equalsIgnoreCase("Linestring") && jg_type != 6 && jg_type != 2) {
                objs_type[i + 1] = 2;
                objs_type[i + 2] = 1;
            }
            if (geom_type.equalsIgnoreCase("Polygon") && jg_type != 7 && jg_type != 3) {
                objs_type[i + 1] = 1003;
                objs_type[i + 2] = 1;
            }
            if (geom_type.equalsIgnoreCase("MultiPoint") && jg_type != 5) {
                objs_type[i + 1] = 1;
                objs_type[i + 2] = 1;
            }
            if (geom_type.equalsIgnoreCase("MultiLinestring") && jg_type != 6 && jg_type != 2) {
                objs_type[i + 1] = 2;
                objs_type[i + 2] = 1;
            }
            if (geom_type.equalsIgnoreCase("MultiPolygon") && jg_type != 7 && jg_type != 3) {
                objs_type[i + 1] = 1003;
                objs_type[i + 2] = 1;
            }
        }
        return objs_type;
    }

}
