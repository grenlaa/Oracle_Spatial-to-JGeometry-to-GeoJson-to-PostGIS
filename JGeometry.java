/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package converter_JGeometry;

import java.sql.*;
import oracle.sql.*;
import java.util.*;

/**
 * Копия JGeometry из sdoapi
 * Просто если интересо что же там внутри JGeometry
 * 
 * P.S. В данном файле распологаются только необходимые функции и конструкторы для класса JGeometry(по моему мнению).
 *  
 */
public class JGeometry {

    static final long serialVersionUID = -4792272186565640701L;
    public static final int GTYPE_POINT = 1;
    public static final int GTYPE_CURVE = 2;
    public static final int GTYPE_POLYGON = 3;
    public static final int GTYPE_COLLECTION = 4;
    public static final int GTYPE_MULTIPOINT = 5;
    public static final int GTYPE_MULTICURVE = 6;
    public static final int GTYPE_MULTIPOLYGON = 7;
    protected static final int ETYPE_UNKNOWN = 0;
    protected static final int ETYPE_POINT = 1;
    protected static final int ETYPE_CURVE = 2;
    protected static final int ETYPE_RING = 3;
    protected static final int ETYPE_COMPOUNDCURVE = 4;
    protected static final int ETYPE_COMPOUNDRING = 5;
    protected static final int EITPR_UNKNOWN = 0;
    protected static final int EITPR_LINEAR = 1;
    protected static final int EITPR_ARC = 2;
    protected static final int EITPR_RECTANGLE = 3;
    protected static final int EITPR_CIRCLE = 4;
    protected static final int EITPR_GEODETICMBR = 3;
    protected static final int ETOPO_NA = 0;
    protected static final int ETOPO_UNKNOWN = 0;
    protected static final int ETOPO_EXTERIOR = 1;
    protected static final int ETOPO_INTERIOR = 2;
//    protected static StructDescriptor geomDesc;
//    protected static StructDescriptor pointDesc;
//    protected static ArrayDescriptor elemInfoDesc;
//    protected static ArrayDescriptor ordinatesDesc;
    protected int gtype;
    protected int linfo;
    protected int srid;
    protected double x;
    protected double y;
    protected double z;
    protected int[] elemInfo;
    protected double[] ordinates;
    protected double[] mbr;
    protected int dim;
//    protected LT_transform lttpH;
//    protected Gc_trans gtransH;
    private static final double MERCATOR_mdFE = 0.0;
    private static final double MERCATOR_mdFN = 0.0;
    private static final double MERCATOR_a = 6378137.0;
    private static final double MERCATOR_k0 = 1.0;
    private static final double MERCATOR_lon0 = 0.0;
    private static final double MERCATOR_f3785 = 0.0;
    private static final double MERCATOR_f54004 = 0.0033528106647474805;
//    private static final double MERCATOR_e3785;
//    private static final double MERCATOR_e54004;
//    private static final double MERCATOR_B;

    protected JGeometry(final int n, final int n2) {
        this.gtype = 0;
        this.linfo = 0;
        this.srid = 0;
        this.x = Double.NaN;
        this.y = Double.NaN;
        this.z = Double.NaN;
        this.elemInfo = null;
        this.ordinates = null;
        this.mbr = null;
        this.dim = 2;
        this.gtype = n % 100;
        this.linfo = n % 1000 / 100;
        this.dim = ((n / 1000 > 0) ? (n / 1000) : 2);
        this.srid = ((n2 <= 0) ? 0 : n2);
    }

    public JGeometry(final int n, final int n2, final double x, final double y, final double z, final int[] array, final double[] array2) {
        this(n, n2);
        this.x = x;
        this.y = y;
        this.z = z;
        if (array == null || array2 == null) {
            this.elemInfo = array;
            this.ordinates = array2;
        } else if (etype0_exists(array) && !ordOffset0_exists(array)) {
            final ArrayList<Integer> list = new ArrayList<Integer>();
            final ArrayList<Double> list2 = new ArrayList<Double>();

            remove_etype0(array, array2, list, list2);

            final int[] elemInfo = new int[list.size()];
            final double[] ordinates = new double[list2.size()];

            for (int i = 0; i < list.size(); ++i) {
                elemInfo[i] = list.get(i);
            }
            for (int j = 0; j < list2.size(); ++j) {
                ordinates[j] = list2.get(j);
            }

            this.elemInfo = elemInfo;
            this.ordinates = ordinates;
        } else {
            this.elemInfo = array;
            this.ordinates = array2;
        }
    }

    public JGeometry(final int n, final int n2, final int[] array, final double[] array2) {
        this(n, n2);
        if (array == null || array2 == null) {
            this.elemInfo = array;
            this.ordinates = array2;
        } else if (etype0_exists(array) && !ordOffset0_exists(array)) {
            final ArrayList<Integer> list = new ArrayList<Integer>();
            final ArrayList<Double> list2 = new ArrayList<Double>();

            remove_etype0(array, array2, list, list2);

            final int[] elemInfo = new int[list.size()];
            final double[] ordinates = new double[list2.size()];

            for (int i = 0; i < list.size(); ++i) {
                elemInfo[i] = list.get(i);
            }

            for (int j = 0; j < list2.size(); ++j) {
                ordinates[j] = list2.get(j);
            }

            this.elemInfo = elemInfo;
            this.ordinates = ordinates;
        } else {
            this.elemInfo = array;
            this.ordinates = array2;
        }
    }

    public JGeometry(final double x, final double y, final int n) {
        this(1, n);
        this.x = x;
        this.y = y;
    }

    public JGeometry(final double x, final double y, final double z, final int n) {
        this(3001, n);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public JGeometry(final double n, final double n2, final double n3, final double n4, final int n5) {
        this(3, n5);
        (this.elemInfo = new int[3])[0] = 1;
        this.elemInfo[1] = 1003;
        this.elemInfo[2] = 3;
        (this.ordinates = new double[4])[0] = n;
        this.ordinates[1] = n2;
        this.ordinates[2] = n3;
        this.ordinates[3] = n4;
    }

    public static final JGeometry load(final STRUCT struct) throws SQLException {
        final Datum[] oracleAttributes = struct.getOracleAttributes();

        /*SDO_GTYPE
        n-DLTT
        D обозначает количество измерений (2, 3 или 4)
        
        L определяет размерность линейной системы координат для трехмерной геометрии линейной системы координат (LRS), 
        то есть размерность (3 или 4), содержащую значение измерения. Для геометрии, отличной от LRS, или для принятия
        пространственного значения по умолчанию для последней размерности в качестве измерения для геометрии LRS укажите 0.

        TT определяет тип геометрии (от 00 до 09, от 10 до 99 зарезервировано для дальнейшего использования).
        
         */
        final int n = (oracleAttributes[0] != null) ? oracleAttributes[0].intValue() : 0;

        //SDO_SRID
        final int n2 = (oracleAttributes[1] != null) ? oracleAttributes[1].intValue() : 0;

        //SDO_ POINT (x/y/z/)
        final STRUCT struct2 = (STRUCT) oracleAttributes[2];
        double doubleValue = Double.NaN;
        double doubleValue2 = Double.NaN;
        double doubleValue3 = Double.NaN;
        if (struct2 != null) {
            final Datum[] array = struct2.getOracleAttributes();
            // x/y
            if (array[0] != null && array[1] != null) {
                doubleValue = array[0].doubleValue();
                doubleValue2 = array[1].doubleValue();
            }
            // z
            if (array[2] != null) {
                doubleValue3 = array[2].doubleValue();
            }
        }

        //SDO_ELEM_INFO (ARRAY) / Массив для интерпритации ординат в SDO_ORDINATES
        final int[] array2 = (int[]) ((oracleAttributes[3] != null) ? ((ARRAY) oracleAttributes[3]).getIntArray() : null);

        //SDO_ORDINATES (ARRAY) / Массив ординат
        double[] doubleArray = null;
        if (oracleAttributes[4] != null) {
            // Находим L 
            if (n % 1000 / 100 == 0) {
                doubleArray = ((ARRAY) oracleAttributes[4]).getDoubleArray();
            } else {
                final int n3 = n % 1000 / 100;
                final int n4 = (n / 1000 > 0) ? (n / 1000) : 2;
                final Datum[] oracleArray = ((ARRAY) oracleAttributes[4]).getOracleArray();
                final int length = oracleArray.length;
                doubleArray = new double[length];
                if (n4 == 2 || ((n4 != 3 || n4 != n3) && (n4 != 4 || (n3 != 3 && n3 != 4)))) {
                    throw new SQLException("An invalid sdo_gtype is found");
                }
                for (int i = 0; i < length; ++i) {
                    if (oracleArray[i] == null && i % n4 != n3 - 1) {
                        throw new SQLException("An invalid null value is found in LRS sdo_ordinates");
                    }
                    if (oracleArray[i] != null) {
                        doubleArray[i] = oracleArray[i].doubleValue();
                    } else {
                        doubleArray[i] = Double.NaN;
                    }
                }
            }
        }

        // Геометрическая фигруа
        if (doubleArray != null && array2 != null) {
            return new JGeometry(n, n2, doubleValue, doubleValue2, doubleValue3, array2, doubleArray);
        }

        // Пустой элемент
        if (Double.isNaN(doubleValue) || Double.isNaN(doubleValue2)) {
            return null;
        }

        //Точка в 3-мерном измерении
        if (!Double.isNaN(doubleValue3)) {
            return new JGeometry(doubleValue, doubleValue2, doubleValue3, n2);
        }

        //Точка в 2-мерном измерении
        return new JGeometry(doubleValue, doubleValue2, n2);
    }

    // Поиск элементов с типом геометрии, не поддерживаемых Oracle Spatial. SDO_ETYPE=0
    private static boolean etype0_exists(final int[] array) {
        boolean b = false;
        for (int i = 0; i < array.length / 3; ++i) {
            if (array[3 * i + 1] == 0) {
                b = true;
                break;
            }
        }
        return b;
    }

    // Поиск элементов которые начинаются с 0 позиции в SDO_GEOMETRY. SDO_STARTING_OFFSET=0
    private static boolean ordOffset0_exists(final int[] array) {
        boolean b = false;
        for (int i = 0; i < array.length / 3; ++i) {
            if (array[3 * i] < 1) {
                b = true;
                break;
            }
        }
        return b;
    }

    // Функия удаления неизвестной геометрии.
    // Оригинал что достал из пакета (remove_etype0_version_old) -> переделал на remove_etype0
    protected static void remove_etype0_version_old(final int[] array, final double[] array2, final ArrayList list, final ArrayList list2) {
        final int[] array3 = new int[array.length];
        final double[] array4 = new double[array2.length];
        int n = 0;
        int n2 = 0;
        int n3 = 0;

        for (int i = 0; i < array.length / 3; ++i) {
            if (array[3 * i + 1] == 0) {
                final int n4 = array[3 * i] - 1;
                int n5;
                if (3 * (i + 1) <= array.length - 1) {
                    n5 = array[3 * (i + 1)] - 2;
                } else {
                    n5 = array2.length - 1;
                }
                n3 += n5 - n4 + 1;
            } else {
                final int n6 = array[3 * i] - 1;
                int n7;
                if (3 * (i + 1) <= array.length - 1) {
                    n7 = array[3 * (i + 1)] - 2;
                } else {
                    n7 = array2.length - 1;
                }
                for (int j = n6; j <= n7; ++j) {
                    array4[n2] = array2[j];
                    ++n2;
                }
                array3[n] = array[3 * i] - n3;
                ++n;
                for (int k = 1; k < 3; ++k) {
                    array3[n] = array[3 * i + k];
                    ++n;
                }
            }
        }
        for (int l = 0; l < n; ++l) {
            list.add(array3[l]);
        }
        for (int n8 = 0; n8 < n2; ++n8) {
            list2.add(array4[n8]);
        }
    }

    // Функия удаления неизвестной геометрии
    // Можно передеать вот так 
    protected static void remove_etype0(final int[] array, final double[] array2, final ArrayList list, final ArrayList list2) {
        int n;
        int n2;

        for (int i = 0; i < array.length / 3; ++i) {
            if (3 * (i + 1) <= array.length - 1) {
                n2 = array[3 * (i + 1)];
                n = array[3 * i];
            } else {
                n2 = array2.length + 1;
                n = array[3 * i];
            }
            if (array[3 * i + 1] != 0) {

                list.add(list2.size() + 1);
                list.add(array[i * 3 + 1]);
                list.add(array[i * 3 + 2]);

                for (int j = n; j < n2; ++j) {
                    list2.add(array2[j - 1]);
                }

            }
        }

    }

}
