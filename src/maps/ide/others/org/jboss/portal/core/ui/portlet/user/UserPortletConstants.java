/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.jboss.portal.core.ui.portlet.user;

/** @author <a href="theute@jboss.org">Thomas Heute</a> $Revision: 5448 $ */
public class UserPortletConstants
{

   public static final String SALT = "14m1r0nm4n";

   public static final String INFOMESSAGE = "infomessage";
   public static final String ERRORMESSAGE = "errormessage";

   // Cookie names
   public static String CK_USERNAME = "username";
   public static String CK_PASS = "password";

   // Default values
   public static int DEFAULT_USERSPERPAGE = 10;

   // Status return codes for the login.
   public static final int LOGIN_STATUS_OK = 0;
   public static final int LOGIN_STATUS_BAD_PASSWORD = 1;
   public static final int LOGIN_STATUS_NO_SUCH_USER = 2;
   public static final int LOGIN_STATUS_USER_DISABLED = 3;
   public static final int LOGIN_STATUS_INVALID_NAME = 4;
   public static final int LOGIN_STATUS_UNEXPECTED_ERROR = 5;

   public static final int PERMANENT_USER_MAX_INACTIVE = 60 * 60 * 24 * 5 * 1000; // 5 days in ms
   public static final int TRANSIENT_USER_MAX_INACTIVE = 60 * 60; // 1 hours in seconds

   public static final String HASH = "hash";
   public static final String USERID = "userid";

   // Portlet configuration

   public static final String EMAILFROM = "emailFrom";
   public static final String SUBSCRIPTIONMODE = "subscriptionMode";
   public static final String SUBSCRIPTIONMODE_AUTOMATIC = "automatic";
   public static final String SUBSCRIPTIONMODE_EMAILVERIFICATION = "emailVerification";
   public static final String DEFAULT_ROLE = "defaultRole";
   public static final long INVALID_MERCHANT_ID = -100;
   public static final int INVALID_ROLE = -100;
   public static final int ROOT = 0;
   public static final int SPA = 1;
   public static final int NPM = 2;
   public static final int NON_STANDARD_ROLE = 1;
   public static final int ALL_ROLES = -1;
   
   /** Timezone information : ((value + 1) * 2) - 1 = 2 * value + 1 */
   public static final String[] TIME_ZONE_OFFSETS =
      {
         "(GMT -12:00 hours) Eniwetok, Kwajalein",
         null,
         "(GMT -11:00 hours) Midway Island, Samoa",
         null,
         "(GMT -10:00 hours) Hawaii",
         null,
         "(GMT -9:00 hours) Alaska",
         null,
         "(GMT -8:00 hours) Pacific Time (US & Canada)",
         null,
         "(GMT -7:00 hours) Mountain Time (US & Canada)",
         null,
         "(GMT -6:00 hours) Central Time (US & Canada), Mexico City",
         null,
         "(GMT -5:00 hours) Eastern Time (US & Canada), Bogota, Lima, Quito",
         null,
         "(GMT -4:00 hours) Atlantic Time (Canada), Caracas, La Paz",
         "(GMT -3:30 hours) Newfoundland",
         "(GMT -3:00 hours) Brazil, Buenos Aires, Georgetown",
         null,
         "(GMT -2:00 hours) Mid-Atlantic",
         null,
         "(GMT -1:00 hours) Azores, Cape Verde Islands",
         null,
         "(GMT) Western Europe Time, London, Lisbon, Casablanca, Monrovia",
         null,
         "(GMT +1:00 hours) CET(Central Europe Time), Brussels, Copenhagen, Madrid, Paris",
         null,
         "(GMT +2:00 hours) EET(Eastern Europe Time), Kaliningrad, South Africa",
         null,
         "(GMT +3:00 hours) Baghdad, Kuwait, Riyadh, Moscow, St. Petersburg",
         "(GMT +3:30 hours) Tehran",
         "(GMT +4:00 hours) Abu Dhabi, Muscat, Baku, Tbilisi",
         "(GMT +4:30 hours) Kabul",
         "(GMT +5:00 hours) Ekaterinburg, Islamabad, Karachi, Tashkent",
         "(GMT +5:30 hours) Bombay, Calcutta, Madras, New Delhi",
         "(GMT +6:00 hours) Almaty, Dhaka, Colombo",
         null,
         "(GMT +7:00 hours) Bangkok, Hanoi, Jakarta",
         null,
         "(GMT +8:00 hours) Beijing, Perth, Singapore, Hong Kong, Chongqing, Urumqi, Taipei",
         null,
         "(GMT +9:00 hours) Tokyo, Seoul, Osaka, Sapporo, Yakutsk",
         "(GMT +9:30 hours) Adelaide, Darwin",
         "(GMT +10:00 hours) EAST(East Australian Standard)",
         null,
         "(GMT +11:00 hours) Magadan, Solomon Islands, New Caledonia",
         null,
         "(GMT +12:00 hours) Auckland, Wellington, Fiji, Kamchatka, Marshall Island",
         null
      };

   public static final String DEFAULT_IMAGES_PATH = "images/user";
}
