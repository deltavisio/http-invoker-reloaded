/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.springframework.remoting.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * @author Rick Evans
 */
class RemoteInvocationUtilsTests {

  @Test
  void fillInClientStackTraceIfPossibleSunnyDay() throws Exception {
    try {
      throw new IllegalStateException("Mmm");
    } catch (Exception ex) {
      int originalStackTraceLngth = ex.getStackTrace().length;
      RemoteInvocationUtils.fillInClientStackTraceIfPossible(ex);
      assertThat(ex.getStackTrace()).as("Stack trace not being filled in")
          .hasSizeGreaterThan(originalStackTraceLngth);
    }
  }

  @SuppressWarnings("java:S2699")
  @Test
  void fillInClientStackTraceIfPossibleWithNullThrowable() {
    // just want to ensure that it doesn't bomb
    RemoteInvocationUtils.fillInClientStackTraceIfPossible(null);
  }

}
