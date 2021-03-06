/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the "License").  You may not use this file except
 * in compliance with the License.
 * 
 * You can obtain a copy of the license at
 * https://jwsdp.dev.java.net/CDDLv1.0.html
 * See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * HEADER in each file and include the License file at
 * https://jwsdp.dev.java.net/CDDLv1.0.html  If applicable,
 * add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your
 * own identifying information: Portions Copyright [yyyy]
 * [name of copyright owner]
 */
 
package samples.ubl.report.facade;

import org.oasis.ubl.commonaggregatecomponents.AddressType;

/**
 * The <code>AddressFacade</code> class provides a set of read-only
 * methods for accessing data in a UBL <code>AddressType</code>.
 *
 * @author Sun Microsystems, Inc.
 * @version 1.0
 */
public class AddressFacade {

    private AddressType address;
  
    /**
     * Creates a new <code>AddressFacade</code> instance.
     *
     * @param addr an <code>AddressType</code> value
     */
    public AddressFacade(AddressType addr) {
        address = addr;
    }

    /**
     * <code>getStreet</code> returns a <code>String</code> representing the
     * street in a UBL address.
     *
     * @return a <code>String</code> representing the street in a UBL address.
     */
    public String getStreet() {
        String result = "";
        try {
            result = address.getStreetName().getValue();
        } catch (NullPointerException npe) {
        }
        return result;
    }

    /**
     * <code>getState</code> returns a <code>String</code> representing the
     * state in a UBL address.
     *
     * @return a <code>String</code> representing the state in a UBL address.
     */
    public String getState() {
        String result = "";
        try {
            result = address.getCountrySubentityCode().getValue();
        } catch (NullPointerException npe) {
        }
        return result;
    }

    /**
     * <code>getCity</code> returns a <code>String</code> representing the city
     * in a UBL address.
     *
     * @return a <code>String</code> representing the city in a UBL address.
     */
    public String getCity() {
        String result = "";
        try {
            result = address.getCityName().getValue();
        } catch (NullPointerException npe) {
        }
        return result;
    }

    /**
     * <code>getZip</code> returns a <code>String</code representing the postal
     * zone in a UBL address.
     *
     * @return a <code>String</code> representing the postal zone in a UBL address.
     */
    public String getZip() {
        String result = "";
        try {
            result = address.getPostalZone().getValue();
        } catch (NullPointerException npe) {
        }
        return result;
    }
}
