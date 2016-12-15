package ftc.evlib.util;

import com.google.common.io.BaseEncoding;

import ftc.electronvolts.util.files.Converter;
import ftc.electronvolts.util.files.Converters;
import ftc.electronvolts.util.files.UtilConverters;
import ftc.evlib.vision.processors.BeaconColorResult;
import ftc.evlib.vision.processors.BeaconName;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 12/1/16
 *
 * This is an implementation of Converters that extends UtilConverters from the state machine framework
 * It adds Converter objects for different classes in EVLib
 */

public class EVConverters extends UtilConverters {

    static {
        converterMap.put(BeaconColorResult.BeaconColor.class, new Converter<BeaconColorResult.BeaconColor>() {

            @Override
            public String toString(BeaconColorResult.BeaconColor object) {
                return object.name();
            }

            @Override
            public BeaconColorResult.BeaconColor fromString(String string) {
                return BeaconColorResult.BeaconColor.valueOf(string);
            }
        });
        converterMap.put(BeaconColorResult.class, new Converter<BeaconColorResult>() {

            @Override
            public String toString(BeaconColorResult object) {
                return object.toString();
            }

            @Override
            public BeaconColorResult fromString(String string) {
                String[] parts = string.split(" *\\| *");
                if (parts.length != 2) return null;
                return new BeaconColorResult(BeaconColorResult.BeaconColor.valueOf(parts[0]), BeaconColorResult.BeaconColor.valueOf(parts[1]));
            }
        });
        converterMap.put(BeaconName.class, new Converter<BeaconName>() {

            @Override
            public String toString(BeaconName object) {
                return object.name();
            }

            @Override
            public BeaconName fromString(String string) {
                return BeaconName.valueOf(string);
            }
        });
        converterMap.put(byte[].class, new Converter<byte[]>() {

            @Override
            public String toString(byte[] object) {
                return BaseEncoding.base64Url().encode(object);
            }

            @Override
            public byte[] fromString(String string) {
                return BaseEncoding.base64Url().decode(string);
            }
        });
    }

    private static final Converters INSTANCE = new EVConverters();

    public static Converters getInstance() {
        return INSTANCE;
    }

    protected EVConverters() {
        super();
    }
}
