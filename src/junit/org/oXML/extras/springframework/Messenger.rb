require 'java'

include_class 'org.oXML.extras.springframework.Messenger'

class RubyMessenger < Messenger

 def setMessage(message)
  @@message = message
 end

 def getMessage
  @@message
 end
end