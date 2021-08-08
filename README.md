# myorderbook
An order book implementation excercise.
A typical use case would be: it receives orders from clients or other systesms and fills from exchanges
It expects 4 types of incoming messages:
  1. New Order
  2. Order Amemd
  3. Orcer Cancel
  4. Order fill (Trade)
Upon receiving the incoming data, it will process it and update the order book accordingly.
