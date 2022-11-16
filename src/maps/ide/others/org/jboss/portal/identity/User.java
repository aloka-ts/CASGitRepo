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
package org.jboss.portal.identity;

import org.jboss.portal.common.p3p.P3PConstants;

import com.genband.m5.maps.common.entity.Organization;

/**
 * A user.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @author <a href="mailto:mageshbk@jboss.com">Magesh Kumar Bojan </a>
 * @version $Revision: 5933 $
 */
public interface User
{
   public static final String INFO_USER_NAME_GIVEN = P3PConstants.INFO_USER_NAME_GIVEN;
   public static final String INFO_USER_NAME_FAMILY = P3PConstants.INFO_USER_NAME_FAMILY;
   public static final String INFO_USER_LOCATION = "portal.user.location";
   public static final String INFO_USER_OCCUPATION = "portal.user.occupation";
   public static final String INFO_USER_EXTRA = "portal.user.extra";
   public static final String INFO_USER_SIGNATURE = "portal.user.signature";
   public static final String INFO_USER_INTERESTS = "portal.user.interests";
   public static final String INFO_USER_LOCALE = "portal.user.locale";
   public static final String INFO_USER_IM_ICQ = "portal.user.im.icq";
   public static final String INFO_USER_IM_AIM = "portal.user.im.aim";
   public static final String INFO_USER_IM_MSNM = "portal.user.im.msnm";
   public static final String INFO_USER_IM_YIM = "portal.user.im.yim";
   public static final String INFO_USER_IM_SKYPE = "portal.user.im.skype";
   public static final String INFO_USER_HOMEPAGE = "portal.user.homepage";
   public static final String INFO_USER_TIME_ZONE_OFFSET = "portal.user.time-zone-offset";
   public static final String INFO_USER_THEME = "portal.user.theme";
   public static final String INFO_USER_SECURITY_QUESTION = "portal.user.security.question";
   public static final String INFO_USER_SECURITY_ANSWER = "portal.user.security.answer";
   public static final String INFO_USER_EMAIL_FAKE = "portal.user.email.fake";
   public static final String INFO_USER_VIEW_EMAIL_VIEW_REAL = "portal.user.email.view-real";
   public static final String INFO_USER_LAST_LOGIN_DATE = "portal.user.last-login-date";
   public static final String INFO_USER_REGISTRATION_DATE = "portal.user.registration-date";

   public static final String INFO_USER_ENABLED = "portal.user.enabled";
   public static final String INFO_USER_EMAIL_REAL = P3PConstants.INFO_USER_BUSINESS_INFO_ONLINE_EMAIL;




   /** The user identifier. */
   public Object getId();

   /** The user name. */
   public String getUserName();

   /** Set the password using proper encoding. */
   public void updatePassword(String password);

   /** Return true if the password is valid. */
   public boolean validatePassword(String password);
   
   /** returns associated merchant */
   public Organization getMerchantAccount();
}
