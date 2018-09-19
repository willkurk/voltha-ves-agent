#
# Copyright 2018- Cisco
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

#list out all alarm names received on the dmaap for integration testing

import json
import urllib2
result = json.load(urllib2.urlopen("http://0.0.0.0:30227/events/unauthenticated.SEC_FAULT_OUTPUT/group1/2?timeout=3000"))
#print (result[0])
for i in result:
	print (json.loads(i)["event"]["commonEventHeader"]["eventName"])
