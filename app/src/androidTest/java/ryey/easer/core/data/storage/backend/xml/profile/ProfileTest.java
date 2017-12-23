/*
 * Copyright (c) 2016 Rui Zhao <renyuneyun@gmail.com>
 *
 * This file is part of Easer.
 *
 * Easer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Easer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Easer.  If not, see <http://www.gnu.org/licenses/>.
 */

package ryey.easer.core.data.storage.backend.xml.profile;

import org.junit.BeforeClass;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ryey.easer.commons.IllegalStorageDataException;
import ryey.easer.commons.plugindef.operationplugin.OperationData;
import ryey.easer.core.data.ProfileStructure;
import ryey.easer.core.data.storage.backend.UnableToSerializeException;
import ryey.easer.plugins.operation.bluetooth.BluetoothOperationData;
import ryey.easer.plugins.operation.bluetooth.BluetoothOperationPlugin;
import ryey.easer.plugins.operation.cellular.CellularOperationData;
import ryey.easer.plugins.operation.cellular.CellularOperationPlugin;

import static org.junit.Assert.assertEquals;

public class ProfileTest {

    public static String t_xml;
    public static ProfileStructure t_profile;

    private static List<String> t_name = new ArrayList<>();
    private static List<OperationData> t_data = new ArrayList<>();

    @BeforeClass
    public static void setUpAll() {
        t_xml = "<?xml version='1.0' encoding='utf-8' standalone='no' ?><profile><name>myTest</name><item spec=\"cellular\"><state>off</state></item><item spec=\"bluetooth\"><state>on</state></item></profile>";

        t_name.add(new CellularOperationPlugin().id());
        t_data.add(new CellularOperationData(false));
                t_name.add(new BluetoothOperationPlugin().id());
        t_data.add(new BluetoothOperationData(true));

        t_profile = new ProfileStructure();
        t_profile.setName("myTest");
        for (int i = 0; i < t_name.size(); i++) {
            t_profile.set(t_name.get(i), t_data.get(i));
        }
    }

    @Test
    public void testParse() throws IOException, XmlPullParserException, IllegalStorageDataException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(t_xml.getBytes());
        ProfileParser profileParser = new ProfileParser();
        ProfileStructure profile = profileParser.parse(byteArrayInputStream);
        assertEquals("myTest", profile.getName());
        Collection<OperationData> operationDataCollection;
        operationDataCollection = profile.get(new CellularOperationPlugin().id());
        assertEquals(operationDataCollection.size(), 1);
        assertEquals(operationDataCollection.iterator().next(), t_data.get(0));
        operationDataCollection = profile.get(new BluetoothOperationPlugin().id());
        assertEquals(operationDataCollection.size(), 1);
        assertEquals(operationDataCollection.iterator().next(), t_data.get(1));
        byteArrayInputStream.close();
    }

    @Test
    public void testSerialize() throws IOException, UnableToSerializeException {
        ProfileSerializer profileSerializer = new ProfileSerializer();
        String xml = profileSerializer.serialize(t_profile);
        assertEquals(t_xml, xml);
    }
}