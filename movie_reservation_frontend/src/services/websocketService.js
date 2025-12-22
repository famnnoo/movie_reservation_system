import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

class WebSocketService {
  constructor() {
    this.client = null
    this.connected = false
    this.subscriptions = new Map()
  }

  connect() {
    return new Promise((resolve, reject) => {
      if (this.connected) {
        resolve()
        return
      }

      this.client = new Client({
        webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
        debug: (str) => {
          console.log('[WebSocket]', str)
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
      })

      this.client.onConnect = () => {
        console.log('WebSocket connected')
        this.connected = true
        resolve()
      }

      this.client.onStompError = (frame) => {
        console.error('WebSocket error:', frame.headers.message, frame.body)
        reject(new Error(frame.headers.message))
      }

      this.client.onWebSocketClose = () => {
        console.log('WebSocket connection closed')
        this.connected = false
      }

      this.client.activate()
    })
  }

  disconnect() {
    if (this.client) {
      this.subscriptions.forEach((subscription) => subscription.unsubscribe())
      this.subscriptions.clear()
      this.client.deactivate()
      this.connected = false
    }
  }

  /**
   * Subscribe to seat updates for a specific display time
   */
  subscribeToSeats(displayTimeId, callback) {
    if (!this.connected) {
      console.error('WebSocket not connected')
      return null
    }

    const topic = `/topic/seats/${displayTimeId}`
    const subscription = this.client.subscribe(topic, (message) => {
      try {
        const data = JSON.parse(message.body)
        callback(data)
      } catch (error) {
        console.error('Error parsing seat update:', error)
      }
    })

    this.subscriptions.set(displayTimeId, subscription)
    return subscription
  }

  /**
   * Unsubscribe from seat updates
   */
  unsubscribeFromSeats(displayTimeId) {
    const subscription = this.subscriptions.get(displayTimeId)
    if (subscription) {
      subscription.unsubscribe()
      this.subscriptions.delete(displayTimeId)
    }
  }

  /**
   * Send message to hold seats (not used - we use HTTP API instead)
   */
  holdSeats(displayTimeId, seatNumbers) {
    if (!this.connected) {
      console.error('WebSocket not connected')
      return
    }

    this.client.publish({
      destination: '/app/seats/hold',
      body: JSON.stringify({
        displayTimeId,
        seatNumbers,
      }),
    })
  }

  /**
   * Send message to release seats (not used - we use HTTP API instead)
   */
  releaseSeats(displayTimeId, seatNumbers) {
    if (!this.connected) {
      console.error('WebSocket not connected')
      return
    }

    this.client.publish({
      destination: '/app/seats/release',
      body: JSON.stringify({
        displayTimeId,
        seatNumbers,
      }),
    })
  }
}

// Export singleton instance
export default new WebSocketService()

